package com.julian.backend.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.application.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.julian.backend.domain.model.Transaction;

public class TransactionServiceTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;

    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository);
    }

    @Test
    void givenNewTransaction_whenSaveTransaction_thenSavesItInRepository() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);

        transactionService.saveTransaction(transaction);

        verify(transactionRepository).save(transaction);
    }
}
