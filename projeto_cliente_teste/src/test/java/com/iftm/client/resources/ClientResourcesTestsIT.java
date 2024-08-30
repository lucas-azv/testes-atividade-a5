package com.iftm.client.resources;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourcesTestsIT {

    @Autowired
    private MockMvc mockMvc;

    // Bruno Vieira
    @Test
    public void testFindByExistingId() throws Exception {
        Long existingId = 7L;

        Client client = new Client(existingId, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0);
        ClientDTO clientDTO = new ClientDTO(client);

        ResultActions result = mockMvc.perform(get("/clients/id/{id}", existingId));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingId.intValue())))
                .andExpect(jsonPath("$.name", is("Jose Saramago")))
                .andExpect(jsonPath("$.cpf", is("10239254871")))
                .andExpect(jsonPath("$.income", is(5000.0)))
                .andExpect(jsonPath("$.birthDate", is("1996-12-23T07:00:00Z")))
                .andExpect(jsonPath("$.children", is(0)));
    }

    // Bruno Vieira
    @Test
    public void testFindByNonExistingId() throws Exception {
        Long nonExistingId = 33L;

        ResultActions result = mockMvc.perform(get("/clients/id/{id}", nonExistingId));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Resource not found")))
                .andExpect(jsonPath("$.path", is("/clients/id/33")));
    }

    // Lucas Borges de Azevedo
    @Test
    public void testFindAllShouldReturnPageOfClients() throws Exception {
        mockMvc.perform(get("/clients")
                .param("page", "0")
                .param("linesPerPage", "12")
                .param("direction", "ASC")
                .param("orderBy", "name")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(12)))
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists());
    }

    // Lucas Borges de Azevedo
    @Test
    public void testFindByIncomeShouldReturnClientsWithSpecifiedIncome() throws Exception {
        Double salarioResultado = 5000.0;

        ResultActions result = mockMvc.perform(get("/clients/income/")
                .param("income", String.valueOf(salarioResultado))
                .param("page", "0")
                .param("linesPerPage", "12")
                .param("direction", "ASC")
                .param("orderBy", "name")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Jose Saramago")))
                .andExpect(jsonPath("$.content[0].income", is(salarioResultado)));
    }

    // Carlos Eduardo Rangel Lima
    @Test
    public void testFindByIncomeGreaterThanShouldReturnClientsAboveSpecifiedIncome() throws Exception {
        Double salarioMinimo = 4000.0;

        ResultActions result = mockMvc.perform(get("/clients/incomeGreaterThan/")
                .param("income", String.valueOf(salarioMinimo))
                .param("page", "0")
                .param("linesPerPage", "5")
                .param("direction", "DESC")
                .param("orderBy", "income")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].name", is("Toni Morrison")))
                .andExpect(jsonPath("$.content[0].income", is(10000.0)))
                .andExpect(jsonPath("$.content[1].name", is("Carolina Maria de Jesus")))
                .andExpect(jsonPath("$.content[1].income", is(7500.0)))
                .andExpect(jsonPath("$.content[2].name", is("Jose Saramago")))
                .andExpect(jsonPath("$.content[2].income", is(5000.0)))
                .andExpect(jsonPath("$.content[3].name", is("Silvio Almeida")))
                .andExpect(jsonPath("$.content[3].income", is(4500.0)));
    }

    // Vinicius Raphael
    @Test
    public void testFindByCpfLikeShouldReturnClientsMatchingCpfPattern() throws Exception {
        String cpfPattern = "102";

        ResultActions result = mockMvc.perform(get("/clients/cpfLike/")
                .param("cpf", cpfPattern)
                .param("page", "0")
                .param("linesPerPage", "12")
                .param("direction", "ASC")
                .param("orderBy", "name")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Jose Saramago")))
                .andExpect(jsonPath("$.content[0].cpf", containsString(cpfPattern)));
    }

    // Vinicius Raphael
    @Test
    public void testUpdateShouldReturnUpdatedClient() throws Exception {
        Long existingId = 7L;
        String updatedName = "Jose Saramago Updated";
        Double updatedIncome = 5500.0;

        String jsonBody = "{ \"name\": \"" + updatedName + "\", \"income\": " + updatedIncome + " }";

        ResultActions result = mockMvc.perform(put("/clients/{id}", existingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingId.intValue())))
                .andExpect(jsonPath("$.name", is(updatedName)))
                .andExpect(jsonPath("$.income", is(updatedIncome)));
    }

    // Vinicius Raphael
    @Test
    public void testUpdateShouldReturnNotFoundForNonExistingId() throws Exception {
        Long nonExistingId = 99L;
        String jsonBody = "{ \"name\": \"Non Existing Client\", \"income\": 1000.0 }";

        ResultActions result = mockMvc.perform(put("/clients/{id}", nonExistingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Resource not found")))
                .andExpect(jsonPath("$.path", is("/clients/id/99")));
    }

}
