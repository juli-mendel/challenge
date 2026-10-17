package com.julian.backend;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.exception.TransactionCycleException;
import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.application.service.TransactionService;
import com.julian.backend.domain.model.Transaction;
import com.julian.backend.infrastructure.db.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionServiceIT {

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
    }

    @Test
    void saveTransaction_withCycle_shouldThrowXXX() {
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
