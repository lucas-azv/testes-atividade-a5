package com.iftm.client.resources;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourcesTestsIT {

    @Autowired
    private MockMvc mockMvc;


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
}
