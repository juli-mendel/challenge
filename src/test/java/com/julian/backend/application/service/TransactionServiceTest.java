package com.julian.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.exception.TransactionCycleException;
import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.application.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.julian.backend.domain.model.Transaction;

public class TransactionServiceTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;
    private static final Long PARENT_ID = 5L;
    private static final Long GRANDPARENT_ID = 3L;

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

        transactionWasSaved(transaction);
    }

    @Test
    void givenTransactionWithExistingParent_whenSaveTransaction_thenSavesItInRepository() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);
        when(transactionRepository.existsById(PARENT_ID)).thenReturn(true);

        transactionService.saveTransaction(transaction);

        transactionWasSaved(transaction);
    }

    @Test
    void givenTransactionWithNonExistingParent_whenSaveTransaction_thenThrowsParentTransactionNotFoundException() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);
        when(transactionRepository.existsById(PARENT_ID)).thenReturn(false);

        assertThrows(ParentTransactionNotFoundException.class, () -> transactionService.saveTransaction(transaction));
        transactionWasNeverSaved();
    }

    @Test
    void givenParentWhoseParentIsTheTransaction_whenSaveTransaction_thenThrowsTransactionCycleException() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);
        Transaction parent = new Transaction(PARENT_ID, TYPE, AMOUNT, ID);
        when(transactionRepository.existsById(PARENT_ID)).thenReturn(true);
        when(transactionRepository.findById(PARENT_ID)).thenReturn(Optional.of(parent));

        assertThrows(TransactionCycleException.class, () -> transactionService.saveTransaction(transaction));
        transactionWasNeverSaved();
    }

    @Test
    void givenAncestorWhoseParentIsTheTransaction_whenSaveTransaction_thenThrowsTransactionCycleException() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);
        Transaction parent = new Transaction(PARENT_ID, TYPE, AMOUNT, GRANDPARENT_ID);
        Transaction grandparent = new Transaction(GRANDPARENT_ID, TYPE, AMOUNT, ID);
        when(transactionRepository.existsById(PARENT_ID)).thenReturn(true);
        when(transactionRepository.findById(PARENT_ID)).thenReturn(Optional.of(parent));
        when(transactionRepository.findById(GRANDPARENT_ID)).thenReturn(Optional.of(grandparent));

        assertThrows(TransactionCycleException.class, () -> transactionService.saveTransaction(transaction));
        transactionWasNeverSaved();
    }

    @Test
    void givenAncestorsWithoutCycle_whenSaveTransaction_thenSavesItInRepository() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);
        Transaction parent = new Transaction(PARENT_ID, TYPE, AMOUNT, GRANDPARENT_ID);
        Transaction grandparent = new Transaction(GRANDPARENT_ID, TYPE, AMOUNT, null);
        when(transactionRepository.existsById(PARENT_ID)).thenReturn(true);
        when(transactionRepository.findById(PARENT_ID)).thenReturn(Optional.of(parent));
        when(transactionRepository.findById(GRANDPARENT_ID)).thenReturn(Optional.of(grandparent));

        transactionService.saveTransaction(transaction);

        transactionWasSaved(transaction);
    }

    @Test
    void givenExistingTransaction_whenGetTransactionSum_thenReturnsSumFromRepository() {
        double repositorySum = 6200.0;
        when(transactionRepository.getTransactionSumById(ID)).thenReturn(Optional.of(repositorySum));

        assertEquals(repositorySum, transactionService.getTransactionSum(ID));
    }

    @Test
    void givenTransactionId_whenGetTransactionSum_withNonExistentTransactionId_thenThrowTransactionNotFoundException() {
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionSum(ID));
    }

    @Test
    void givenType_whenGetTransactionIdsByType_withNonExistentType_shouldReturnEmptyList() {
        assertEquals(List.of(), transactionService.getTransactionIdsByType(TYPE));
    }

    @Test
    void givenType_whenThereIsOneTransactionWithThatType_shouldReturnAListWithThatTransaction() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);
        when(transactionRepository.getTransactionIdsByType(TYPE)).thenReturn(List.of(transaction.getId()));

        List<Long> transactionIds = transactionService.getTransactionIdsByType(TYPE);

        assertEquals(1, transactionIds.size());
        assertEquals(transaction.getId(), transactionIds.get(0));
    }

    private void transactionWasSaved(Transaction transaction) {
        verify(transactionRepository).save(transaction);
    }

    private void transactionWasNeverSaved() {
        verify(transactionRepository, never()).save(any());
    }
}
