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
    void getTransactionSumById_givenTransactionWithOneChild_thenParentSumIncludesChild() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, 1000.0, ID));

        double expectedSum = AMOUNT + 1000.0;
        assertEquals(Optional.of(expectedSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void getTransactionSumById_givenChildAndGrandchild_thenSumsTransitively() {
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
    void getTransactionSumById_givenATransactionWithTwoChildren_thenParentSumIncludesBoth() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double firstChildAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, firstChildAmount, ID));

        double secondChildAmount = 30.0;
        transactionRepository.save(new Transaction(SECOND_CHILD_ID, TYPE, secondChildAmount, ID));

        double expectedParentSum = AMOUNT + firstChildAmount + secondChildAmount;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void getTransactionSumById_givenUnconnectedTransaction_thenItIsNotIncluded() {
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
    void getTransactionSumById_givenChildWithNegativeAmount_thenParentSumIsReduced() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = -2000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        double expectedParentSum = AMOUNT + childAmount;
        assertEquals(Optional.of(expectedParentSum), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void getTransactionSumById_givenChildWithZeroAmount_thenParentSumIsUnchanged() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = 0.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        assertEquals(Optional.of(AMOUNT), transactionRepository.getTransactionSumById(ID));
    }

    @Test
    void save_givenExistingTransactionIdWithSameParentAndNewAmount_thenSumsAreUpdated() {
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
    void save_givenExistingTransactionIdWithSameParentAndNewNegativeAmount_thenSumsAreUpdated() {
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

    @Test
    void save_givenExistingTransactionIdWithNewParent_thenSubtreeSumMovesToNewParent() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        double grandchildAmount = 200.0;
        transactionRepository.save(new Transaction(GRANDCHILD_ID, TYPE, grandchildAmount, CHILD_ID));

        double newParentAmount = 4.0;
        transactionRepository.save(new Transaction(UNCONNECTED_ID, TYPE, newParentAmount, null));

        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, UNCONNECTED_ID));

        double expectedChildSum = childAmount + grandchildAmount;
        assertEquals(Optional.of(expectedChildSum), transactionRepository.getTransactionSumById(CHILD_ID));
        assertEquals(Optional.of(AMOUNT), transactionRepository.getTransactionSumById(ID));

        double expectedNewParentSum = newParentAmount + expectedChildSum;
        assertEquals(Optional.of(expectedNewParentSum), transactionRepository.getTransactionSumById(UNCONNECTED_ID));
    }

    @Test
    void save_givenExistingTransactionIdWithParentRemoved_thenSubtreeSumIsRemovedFromOldParent() {
        transactionRepository.save(new Transaction(ID, TYPE, AMOUNT, null));

        double childAmount = 1000.0;
        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, ID));

        double grandchildAmount = 200.0;
        transactionRepository.save(new Transaction(GRANDCHILD_ID, TYPE, grandchildAmount, CHILD_ID));

        transactionRepository.save(new Transaction(CHILD_ID, TYPE, childAmount, null));

        double expectedChildSum = childAmount + grandchildAmount;
        assertEquals(Optional.of(expectedChildSum), transactionRepository.getTransactionSumById(CHILD_ID));
        assertEquals(Optional.of(AMOUNT), transactionRepository.getTransactionSumById(ID));
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
