package com.julian.backend.service;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.exception.TransactionCycleException;
import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.application.service.TransactionService;
import com.julian.backend.domain.model.Transaction;
import com.julian.backend.infrastructure.db.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionServiceIntegrationTest {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        InMemoryTransactionRepository inMemoryTransactionRepository = new InMemoryTransactionRepository();
        transactionService = new TransactionService(inMemoryTransactionRepository);
    }

    @Test
    void saveManyTransactions_andGetTransactionSumById() {
        double parentTransactionAmount = 10000.0;
        Transaction parentTransaction = new Transaction(10L, "cars", parentTransactionAmount, null);
        transactionService.saveTransaction(parentTransaction);

        assertEquals(parentTransactionAmount, transactionService.getTransactionSum(parentTransaction.getId()));

        double childTransactionAmount = 1000.0;
        Transaction childTransaction = new Transaction(11L, "cars", childTransactionAmount, parentTransaction.getId());
        transactionService.saveTransaction(childTransaction);

        double newParentSum = parentTransactionAmount + childTransactionAmount;
        assertEquals(newParentSum, transactionService.getTransactionSum(parentTransaction.getId()));
        assertEquals(childTransactionAmount, transactionService.getTransactionSum(childTransaction.getId()));

        double grandchildTransactionAmount = 5000.0;
        Transaction grandchildTransaction = new Transaction(12L, "planes", grandchildTransactionAmount, childTransaction.getId());
        transactionService.saveTransaction(grandchildTransaction);

        assertEquals(newParentSum + grandchildTransactionAmount, transactionService.getTransactionSum(parentTransaction.getId()));
        assertEquals(childTransactionAmount + grandchildTransactionAmount, transactionService.getTransactionSum(childTransaction.getId()));
        assertEquals(grandchildTransactionAmount, transactionService.getTransactionSum(grandchildTransaction.getId()));
    }

    @Test
    void saveManyTransactions_toAnExistentTransaction_withParentRemoved_andGetTransactionSumById() {
        double parentTransactionAmount = 10000.0;
        Transaction parentTransaction = new Transaction(10L, "cars", parentTransactionAmount, null);
        transactionService.saveTransaction(parentTransaction);

        assertEquals(parentTransactionAmount, transactionService.getTransactionSum(parentTransaction.getId()));

        double childTransactionAmount = 1000.0;
        long childId = 11L;
        Transaction childTransaction = new Transaction(childId, "cars", childTransactionAmount, parentTransaction.getId());
        transactionService.saveTransaction(childTransaction);

        double newParentSum = parentTransactionAmount + childTransactionAmount;
        assertEquals(newParentSum, transactionService.getTransactionSum(parentTransaction.getId()));
        assertEquals(childTransactionAmount, transactionService.getTransactionSum(childTransaction.getId()));

        double grandchildTransactionAmount = 5000.0;
        Transaction grandchildTransaction = new Transaction(12L, "planes", grandchildTransactionAmount, childId);
        transactionService.saveTransaction(grandchildTransaction);

        assertEquals(newParentSum + grandchildTransactionAmount, transactionService.getTransactionSum(parentTransaction.getId()));
        assertEquals(childTransactionAmount + grandchildTransactionAmount, transactionService.getTransactionSum(childTransaction.getId()));
        assertEquals(grandchildTransactionAmount, transactionService.getTransactionSum(grandchildTransaction.getId()));

        Transaction newChildTransaction = new Transaction(childId, "cars", childTransactionAmount, null);
        transactionService.saveTransaction(newChildTransaction);

        assertEquals(parentTransactionAmount, transactionService.getTransactionSum(parentTransaction.getId()));
        assertEquals(childTransactionAmount + grandchildTransactionAmount, transactionService.getTransactionSum(childTransaction.getId()));
        assertEquals(grandchildTransactionAmount, transactionService.getTransactionSum(grandchildTransaction.getId()));
    }

    @Test
    void getTransactionSum_withNonExistentId_shouldThrowTransactionNotFoundException() {
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionSum(123L));
    }

    @Test
    void saveTransaction_withNonExistentParentId_shouldThrowParentTransactionNotFoundException() {
        Transaction newTransaction = new Transaction(123L, "cars", 1000.0, 999L);
        assertThrows(ParentTransactionNotFoundException.class, () -> transactionService.saveTransaction(newTransaction));
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionSum(123L));
        assertEquals(List.of(), transactionService.getTransactionsByType("cars"));
    }

    @Test
    void saveTransaction_toAnExistentTransaction_withDifferentParent_shouldMoveItsSumToTheNewParent() {
        double firstParentAmount = 10000.0;
        Transaction firstParent = new Transaction(10L, "cars", firstParentAmount, null);
        transactionService.saveTransaction(firstParent);

        double secondParentAmount = 4.0;
        Transaction secondParent = new Transaction(20L, "cars", secondParentAmount, null);
        transactionService.saveTransaction(secondParent);

        double childAmount = 1000.0;
        long childId = 11L;
        transactionService.saveTransaction(new Transaction(childId, "cars", childAmount, firstParent.getId()));

        double grandchildAmount = 5000.0;
        transactionService.saveTransaction(new Transaction(12L, "planes", grandchildAmount, childId));

        transactionService.saveTransaction(new Transaction(childId, "cars", childAmount, secondParent.getId()));

        double childSum = childAmount + grandchildAmount;
        assertEquals(firstParentAmount, transactionService.getTransactionSum(firstParent.getId()));
        assertEquals(secondParentAmount + childSum, transactionService.getTransactionSum(secondParent.getId()));
        assertEquals(childSum, transactionService.getTransactionSum(childId));
    }

    @Test
    void saveTransaction_toAnExistentTransaction_withDifferentType_shouldMoveItToTheNewType() {
        transactionService.saveTransaction(new Transaction(10L, "cars", 1000.0, null));
        Transaction otherCar = new Transaction(20L, "cars", 2000.0, null);
        transactionService.saveTransaction(otherCar);

        Transaction updatedTransaction = new Transaction(10L, "planes", 1000.0, null);
        transactionService.saveTransaction(updatedTransaction);

        assertEquals(List.of(otherCar), transactionService.getTransactionsByType("cars"));
        assertEquals(List.of(updatedTransaction), transactionService.getTransactionsByType("planes"));
    }

    @Test
    void saveTransaction_withCycle_shouldThrowTransactionCycleException() {
        long parentTransactionId = 123L;
        double parentAmount = 1000.0;
        Transaction parentTransaction = new Transaction(parentTransactionId, "cars", parentAmount, null);
        transactionService.saveTransaction(parentTransaction);

        long newTransactionId = 999L;
        double newTransactionAmount = 2000.0;
        Transaction childTransaction = new Transaction(newTransactionId, "cars", newTransactionAmount, parentTransactionId);
        transactionService.saveTransaction(childTransaction);

        double newParentAmount = 3000.0;
        Transaction newParentTransactionWithCycle = new Transaction(parentTransactionId, "cars", newParentAmount, newTransactionId);

        assertThrows(TransactionCycleException.class, () -> transactionService.saveTransaction(newParentTransactionWithCycle));
        assertEquals(parentAmount + newTransactionAmount, transactionService.getTransactionSum(parentTransactionId));
        assertEquals(newTransactionAmount, transactionService.getTransactionSum(newTransactionId));
    }
}
