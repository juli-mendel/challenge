package com.julian.backend.infrastructure.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.julian.backend.domain.model.Transaction;

public class InMemoryTransactionRepositoryTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;
    private static final Long CHILD_ID = 11L;
    private static final Long GRANDCHILD_ID = 12L;
    private static final Long SECOND_CHILD_ID = 13L;
    private static final Long UNCONNECTED_ID = 20L;

    private InMemoryTransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = new InMemoryTransactionRepository();
    }

    @Test
    void givenSavedTransaction_whenFindById_thenReturnsTransaction() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);

        transactionRepository.save(transaction);

        assertEquals(Optional.of(transaction), transactionRepository.findById(ID));
    }

    @Test
    void givenNonExistingId_whenFindById_thenReturnsEmpty() {
        assertEquals(Optional.empty(), transactionRepository.findById(ID));
    }

    @Test
    void givenSavedTransaction_whenExistsById_thenReturnsTrue() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);

        transactionRepository.save(transaction);

        assertTrue(transactionRepository.existsById(ID));
    }

    @Test
    void givenNonExistingId_whenExistsById_thenReturnsFalse() {
        assertFalse(transactionRepository.existsById(ID));
    }

    @Test
    void givenChildTransaction_whenGetTransactionSumById_thenParentSumIncludesChild() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, 1000.0, ID));

        double expectedSum = AMOUNT + 1000.0;
        assertEquals(Optional.of(expectedSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void givenChildAndGrandchild_whenGetTransactionSumById_thenSumsTransitively() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        double grandchildAmount = 200.0;
        transactionRepository.save(new Transaction(GRANDCHILD_ID, TYPE, grandchildAmount, CHILD_ID));

        double expectedChildSum = childAmount + grandchildAmount;
        assertEquals(Optional.of(expectedChildSum), transactionRepository.getTransactionSumById(CHILD_ID));

        double expectedParentSum = AMOUNT + expectedChildSum;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void givenTwoChildren_whenGetTransactionSumById_thenParentSumIncludesBoth() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double firstChildAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, firstChildAmount, ID));

        double secondChildAmount = 30.0;
        transactionRepository.save(new Transaction(SECOND_CHILD_ID, TYPE, secondChildAmount, ID));

        double expectedParentSum = AMOUNT + firstChildAmount + secondChildAmount;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void givenUnconnectedTransaction_whenGetTransactionSumById_thenItIsNotIncluded() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        double newTreeAmount = 4.0;
        transactionRepository.save(new Transaction(UNCONNECTED_ID, TYPE, newTreeAmount, null));

        double expectedParentSum = AMOUNT + childAmount;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
        assertEquals(Optional.of(newTreeAmount), transactionRepository.getTransactionSumById(UNCONNECTED_ID));
    }

    @Test
    void givenChildWithNegativeAmount_whenGetTransactionSumById_thenParentSumIsReduced() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = -2000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        double expectedParentSum = AMOUNT + childAmount;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void givenChildWithZeroAmount_whenGetTransactionSumById_thenParentSumIsUnchanged() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = 0.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        assertEquals(Optional.of(AMOUNT), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void givenExistingTransactionWithSameParentAndNewAmount_whenSave_thenSumsAreUpdated() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double oldChildAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, oldChildAmount, ID));

        double grandchildAmount = 200.0;
        transactionRepository.save(new Transaction(GRANDCHILD_ID, TYPE, grandchildAmount, CHILD_ID));

        double newChildAmount = 3000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, newChildAmount, ID));

        double expectedChildSum = newChildAmount + grandchildAmount;
        assertEquals(Optional.of(expectedChildSum), transactionRepository.getTransactionSumById(CHILD_ID));

        double expectedParentSum = AMOUNT + expectedChildSum;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void givenExistingTransactionWithSameParentAndNewNegativeAmount_whenSave_thenSumsAreUpdated() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double oldChildAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, oldChildAmount, ID));

        double grandchildAmount = 200.0;
        transactionRepository.save(new Transaction(GRANDCHILD_ID, TYPE, grandchildAmount, CHILD_ID));

        double newChildAmount = -3000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, newChildAmount, ID));

        double expectedChildSum = newChildAmount + grandchildAmount;
        assertEquals(Optional.of(expectedChildSum), transactionRepository.getTransactionSumById(CHILD_ID));

        double expectedParentSum = AMOUNT + expectedChildSum;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
    }
}
