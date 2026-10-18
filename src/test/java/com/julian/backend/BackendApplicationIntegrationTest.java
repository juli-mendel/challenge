package com.julian.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BackendApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenTransactionsLifecycle_whenSavingAndUpdating_thenSumsAndTypesAreConsistent() throws Exception {
        mockMvc.perform(get("/transactions/sum/10")).andExpect(status().isNotFound());
        expectIdsByType("cars", "[]");

        // 10 (cars) <- 11 (shopping) <- 12 (shopping)
        putTransaction(10, """
            {"amount": 5000, "type": "cars"}""");
        putTransaction(11, """
            {"amount": 10000, "type": "shopping", "parent_id": 10}""");
        putTransaction(12, """
            {"amount": 5000, "type": "shopping", "parent_id": 11}""");

        expectSum(10, 20000);
        expectSum(11, 15000);
        expectSum(12, 5000);
        expectIdsByType("cars", "[10]");
        expectIdsByType("shopping", "[11, 12]");

        // 12 changes parent to 10: 11 -> 10 <- 12
        putTransaction(12, """
            {"amount": 5000, "type": "shopping", "parent_id": 10}""");
        expectSum(10, 20000);
        expectSum(11, 10000);

        // update 11 so it has no parent, 10 <- 12 | 11
        putTransaction(11, """
            {"amount": 10000, "type": "shopping"}""");
        expectSum(10, 10000);
        expectSum(11, 10000);

        // 12 changes type: shopping -> cars
        putTransaction(12, """
            {"amount": 5000, "type": "cars", "parent_id": 10}""");
        expectIdsByType("cars", "[10, 12]");
        expectIdsByType("shopping", "[11]");
    }

    private void putTransaction(long id, String body) throws Exception {
        mockMvc.perform(put("/transactions/" + id)
                .contentType(APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                {"status": "ok"}"""));
    }

    private void expectSum(long id, double sum) throws Exception {
        mockMvc.perform(get("/transactions/sum/" + id))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"sum\": " + sum + "}"));
    }

    private void expectIdsByType(String type, String expectedIds) throws Exception {
        mockMvc.perform(get("/transactions/types/" + type))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedIds));
    }
}
