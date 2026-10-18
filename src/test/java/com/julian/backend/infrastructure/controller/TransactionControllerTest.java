package com.julian.backend.infrastructure.controller;

import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.application.usecase.GetTransactionIdsByType;
import com.julian.backend.application.usecase.GetTransactionSum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetTransactionIdsByType getTransactionIdsByType;

    @MockitoBean
    private GetTransactionSum getTransactionSum;

    @Test
    void getTransactionIdsByType_whenNoTransactions_thenReturnsEmptyList() throws Exception {
        when(getTransactionIdsByType.getTransactionIdsByType("someType")).thenReturn(List.of());
        mockMvc.perform(get("/transactions/type/someType"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    void givenNoType_whenGetTransactionIdsByType_thenReturnsError404() throws Exception {
        mockMvc.perform(get("/transactions/type/"))
            .andExpect(status().isNotFound());

        verify(getTransactionIdsByType, never()).getTransactionIdsByType(any());
    }

    @Test
    void getTransactionIdsByType_withExistentType_thenReturnsIdsList() throws Exception {
        when(getTransactionIdsByType.getTransactionIdsByType("someType")).thenReturn(List.of(1L, 2L, 3L));
        mockMvc.perform(get("/transactions/type/someType"))
            .andExpect(status().isOk())
            .andExpect(content().json("[1, 2 ,3]"));
    }

    @Test
    void getTransactionSum_withNonExistentId_thenReturnsError404() throws Exception {
        long invalidTransactionId = 1L;
        when(getTransactionSum.getTransactionSum(invalidTransactionId)).thenThrow(new TransactionNotFoundException(invalidTransactionId));
        mockMvc.perform(get("/transactions/sum/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getTransactionSum_withExistentId_thenReturnsItsSum() throws Exception {
        long validTransactionId = 1L;
        when(getTransactionSum.getTransactionSum(validTransactionId)).thenReturn(100.0);
        mockMvc.perform(get("/transactions/sum/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("100.0"));
    }

    @Test
    void getTransactionSum_withNegativeId_thenReturnsError400() throws Exception {
        mockMvc.perform(get("/transactions/sum/-1"))
            .andExpect(status().isBadRequest());
        verify(getTransactionSum, never()).getTransactionSum(any());
    }

    @Test
    void getTransactionSum_withNonNumericId_thenReturnsError400() throws Exception {
        mockMvc.perform(get("/transactions/sum/abc"))
            .andExpect(status().isBadRequest());
        verify(getTransactionSum, never()).getTransactionSum(any());
    }

    @Test
    void getTransactionSum_withNoId_thenReturnsError404() throws Exception {
        mockMvc.perform(get("/transactions/sum/"))
            .andExpect(status().isNotFound());
        verify(getTransactionSum, never()).getTransactionSum(any());
    }
}
