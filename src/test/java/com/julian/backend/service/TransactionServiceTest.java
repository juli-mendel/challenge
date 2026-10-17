package com.julian.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.application.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.julian.backend.domain.model.Transaction;

public class TransactionServiceTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;
    private static final Long PARENT_ID = 5L;

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

    @Test
    void givenTransactionWithExistingParent_whenSaveTransaction_thenSavesItInRepository() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);
        when(transactionRepository.existsById(PARENT_ID)).thenReturn(true);

        transactionService.saveTransaction(transaction);

        verify(transactionRepository).save(transaction);
    }

    @Test
    void givenTransactionWithNonExistingParent_whenSaveTransaction_thenThrowsParentTransactionNotFoundException() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);
        when(transactionRepository.existsById(PARENT_ID)).thenReturn(false);

        assertThrows(ParentTransactionNotFoundException.class, () -> transactionService.saveTransaction(transaction));
        verify(transactionRepository, never()).save(any());
    }
}
