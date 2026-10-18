package com.julian.backend.infrastructure.controller;

import com.julian.backend.application.usecase.GetTransactionIdsByType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetTransactionIdsByType getTransactionIdsByType;

    @Test
    void getTransactionIdsByType_whenNoTransactions_thenReturnsEmptyList() throws Exception {
        when(getTransactionIdsByType.getTransactionIdsByType("someType")).thenReturn(List.of());
        mockMvc.perform(get("/transactions/type/someType"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    void getTransactionIdsByType_withExistentType_thenReturnsIdsList() throws Exception {
        when(getTransactionIdsByType.getTransactionIdsByType("someType")).thenReturn(List.of(1L, 2L, 3L));
        mockMvc.perform(get("/transactions/type/someType"))
            .andExpect(status().isOk())
            .andExpect(content().json("[1, 2 ,3]"));
    }
}
