package com.iftm.client.resources;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;

//necessário para utilizar o MockMVC
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourceTest {
    @Autowired
    private MockMvc mockMVC;

    @MockBean
    private ClientService service;

    /**
     * Caso de testes : Verificar se o endpoint get/clients/ retorna todos os
     * clientes existentes
     * Arrange:
     * - camada service simulada com mockito
     * - base de dado : 3 clientes
     * new Client(7l, "Jose Saramago", "10239254871", 5000.0,
     * Instant.parse("1996-12-23T07:00:00Z"), 0);
     * new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0,
     * Instant.parse("1996-12-23T07:00:00Z"), 0);
     * new Client(8l, "Toni Morrison", "10219344681", 10000.0,
     * Instant.parse("1940-02-23T07:00:00Z"), 0);
     * - Uma PageRequest default
     * 
     * @throws Exception
     */
    @Test
    @DisplayName("Verificar se o endpoint get/clients/ retorna todos os clientes existentes")
    public void testarEndPointListarTodosClientesRetornaCorreto() throws Exception {
        // arrange
        int quantidadeClientes = 4;

        // Configurando o Mock ClientService
        List<ClientDTO> listaClientes = new ArrayList<>();
        listaClientes.add(new ClientDTO(
                new Client(7L, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));
        listaClientes.add(new ClientDTO(new Client(4L, "Carolina Maria de Jesus", "10419244771", 7500.0,
                Instant.parse("1996-12-23T07:00:00Z"), 0)));
        listaClientes.add(new ClientDTO(
                new Client(8L, "Toni Morrison", "10219344681", 10000.0, Instant.parse("1940-02-23T07:00:00Z"), 0)));
        listaClientes.add(new ClientDTO(
                new Client(13L, "Luiz Inácio", "13131313131", 99999.0, Instant.parse("1930-02-23T07:00:00Z"), 0)));

        Page<ClientDTO> page = new PageImpl<>(listaClientes);

        Mockito.when(service.findAllPaged(Mockito.any())).thenReturn(page);

        // act
        ResultActions resultados = mockMVC.perform(get("/clients/").accept(MediaType.APPLICATION_JSON));

        // assign
        resultados
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 7L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 4L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 8L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 13L).exists())

                // Verifica os detalhes do cliente "Jose Saramago"
                .andExpect(jsonPath("$.content[?(@.id == '%s')].name", 7L).value("Jose Saramago"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].cpf", 7L).value("10239254871"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].income", 7L).value(5000.0))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].birthDate", 7L).value("1996-12-23T07:00:00Z"))

                // Verifica os detalhes do cliente "Carolina Maria de Jesus"
                .andExpect(jsonPath("$.content[?(@.id == '%s')].name", 4L).value("Carolina Maria de Jesus"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].cpf", 4L).value("10419244771"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].income", 4L).value(7500.0))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].birthDate", 4L).value("1996-12-23T07:00:00Z"))

                // Verifica os detalhes do cliente "Toni Morrison"
                .andExpect(jsonPath("$.content[?(@.id == '%s')].name", 8L).value("Toni Morrison"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].cpf", 8L).value("10219344681"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].income", 8L).value(10000.0))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].birthDate", 8L).value("1940-02-23T07:00:00Z"))

                // Verifica os detalhes do cliente "Luiz Inácio"
                .andExpect(jsonPath("$.content[?(@.id == '%s')].name", 13L).value("Luiz Inácio"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].cpf", 13L).value("13131313131"))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].income", 13L).value(99999.0))
                .andExpect(jsonPath("$.content[?(@.id == '%s')].birthDate", 13L).value("1930-02-23T07:00:00Z"))

                // Verifica o total de elementos
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalElements").value(quantidadeClientes));
    }

}
