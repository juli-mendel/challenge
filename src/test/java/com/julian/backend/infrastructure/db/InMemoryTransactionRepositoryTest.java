package com.julian.backend.infrastructure.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.julian.backend.domain.model.Transaction;

public class InMemoryTransactionRepositoryTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final String OTHER_TYPE = "planes";
    private static final String EMPTY_TYPE = "";
    private static final Double AMOUNT = 5000.0;
    private static final Long UNCONNECTED_ID = 20L;

    private InMemoryTransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = new InMemoryTransactionRepository();
    }

    @Test
    void findById_givenSavedTransactionId_thenReturnsTransaction() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);

        transactionRepository.save(transaction);

        assertEquals(Optional.of(transaction), transactionRepository.findById(ID));
    }

    @Test
    void findById_givenNonExistingId_thenReturnsEmpty() {
        assertEquals(Optional.empty(), transactionRepository.findById(ID));
    }

    @Test
    void existsById_givenSavedTransactionId_thenReturnsTrue() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);

        transactionRepository.save(transaction);

        assertTrue(transactionRepository.existsById(ID));
    }

    @Test
    void existsById_givenNonExistingId_thenReturnsFalse() {
        assertFalse(transactionRepository.existsById(ID));
    }

    @Test
    void getTransactionSumById_givenNoStoredSum_thenReturnsEmpty() {
        assertEquals(Optional.empty(), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void save_givenTransaction_thenDoesNotStoreItsSum() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        assertEquals(Optional.empty(), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void putTransactionSum_givenId_thenGetTransactionSumByIdReturnsIt() {
        double sum = 6200.0;

        transactionRepository.putTransactionSum(ID, sum);

        assertEquals(Optional.of(sum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void putTransactionSum_givenStoredSum_thenReplacesIt() {
        transactionRepository.putTransactionSum(ID, 6200.0);

        double newSum = 300.0;
        transactionRepository.putTransactionSum(ID, newSum);

        assertEquals(Optional.of(newSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void addToTransactionSum_givenStoredSum_thenAddsAmount() {
        transactionRepository.putTransactionSum(ID, AMOUNT);

        double amount = 1000.0;
        transactionRepository.addToTransactionSum(ID, amount);

        assertEquals(Optional.of(AMOUNT + amount), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void addToTransactionSum_givenNoStoredSum_thenStoresTheAmountAsSum() {
        double amount = 1000.0;

        transactionRepository.addToTransactionSum(ID, amount);

        assertEquals(Optional.of(amount), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void addToTransactionSum_givenNegativeAmount_thenSubtractsIt() {
        transactionRepository.putTransactionSum(ID, AMOUNT);

        double amount = -2000.0;
        transactionRepository.addToTransactionSum(ID, amount);

        assertEquals(Optional.of(AMOUNT + amount), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void getTransactionIdsByType_givenNoTransactions_thenReturnsEmptyList() {
        assertEquals(List.of(), transactionRepository.getTransactionIdsByType(TYPE));
    }

    @Test
    void getTransactionIdsByType_withOneTransactionWithThatType_thenReturnsIt() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);
        transactionRepository.save(transaction);

        assertEquals(List.of(ID), transactionRepository.getTransactionIdsByType(TYPE));
    }

    @Test
    void getTransactionIdsByType_givenTwoTransactionsWithSameType_thenReturnsBoth() {
        Transaction firstTransaction = new Transaction(ID, TYPE, AMOUNT, null);
        Transaction secondTransaction = new Transaction(UNCONNECTED_ID, TYPE, AMOUNT, null);
        transactionRepository.save(firstTransaction);
        transactionRepository.save(secondTransaction);

        assertEquals(List.of(ID, UNCONNECTED_ID), transactionRepository.getTransactionIdsByType(TYPE));
    }

    @Test
    void getTransactionIdsByType_givenTwoTransactionsWithDifferentTypes_thenReturnsOnlyThoseOfEachType() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);
        Transaction otherTypeTransaction = new Transaction(UNCONNECTED_ID, OTHER_TYPE, AMOUNT, null);
        transactionRepository.save(transaction);
        transactionRepository.save(otherTypeTransaction);

        assertEquals(List.of(ID), transactionRepository.getTransactionIdsByType(TYPE));
        assertEquals(List.of(UNCONNECTED_ID), transactionRepository.getTransactionIdsByType(OTHER_TYPE));
    }

    @Test
    void getTransactionIdsByType_givenTransactionWithEmptyType_thenReturnsIt() {
        Transaction transaction = new Transaction(ID, EMPTY_TYPE, AMOUNT, null);
        transactionRepository.save(transaction);

        assertEquals(List.of(ID), transactionRepository.getTransactionIdsByType(EMPTY_TYPE));
    }

    @Test
    void save_givenExistingTransactionIdWithSameType_thenItAppearsOnceWithItsNewVersion() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        Transaction updatedTransaction = new Transaction(ID, TYPE, 3000.0, null);
        transactionRepository.save(updatedTransaction);

        assertEquals(List.of(ID), transactionRepository.getTransactionIdsByType(TYPE));
    }

    @Test
    void save_givenExistingTransactionIdWithNewType_thenItMovesFromOldTypeToNewType() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        Transaction updatedTransaction = new Transaction(ID, OTHER_TYPE, AMOUNT, null);
        transactionRepository.save(updatedTransaction);

        assertEquals(List.of(), transactionRepository.getTransactionIdsByType(TYPE));
        assertEquals(List.of(ID), transactionRepository.getTransactionIdsByType(OTHER_TYPE));
    }
}
