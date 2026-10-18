package com.julian.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import com.julian.backend.domain.exception.InvalidTransactionException;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;
    private static final Long PARENT_ID = 5L;

    @Test
    void getId_givenNewTransactionWithId_thenReturnsId() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(ID, transaction.getId());
    }

    @Test
    void createTransaction_givenNegativeId_thenThrowsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> new Transaction(-1L, TYPE, AMOUNT, PARENT_ID));
    }

    @Test
    void getType_givenNewTransactionWithType_thenReturnsType() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(TYPE, transaction.getType());
    }

    @Test
    void getType_givenNewTransactionWithEmptyType_thenReturnsEmptyType() {
        Transaction transaction = new Transaction(ID, "", AMOUNT, PARENT_ID);

        assertEquals("", transaction.getType());
    }

    @Test
    void getAmount_givenNewTransactionWithAmount_thenReturnsAmount() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(AMOUNT, transaction.getAmount());
    }

    @Test
    void getAmount_givenNewTransactionWithNegativeAmount_thenReturnsAmount() {
        Transaction transaction = new Transaction(ID, TYPE, -2000.0, PARENT_ID);

        assertEquals(-2000.0, transaction.getAmount());
    }

    @Test
    void getAmount_givenNewTransactionWithZeroAmount_thenReturnsAmount() {
        Transaction transaction = new Transaction(ID, TYPE, 0.0, PARENT_ID);

        assertEquals(0.0, transaction.getAmount());
    }

    @Test
    void getParentId_givenNewTransactionWithParentId_thenReturnsParentId() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(Optional.of(PARENT_ID), transaction.getParentId());
    }

    @Test
    void getParentId_givenNewTransactionWithoutParentId_thenReturnsEmpty() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);

        assertEquals(Optional.empty(), transaction.getParentId());
    }

    @Test
    void createTransaction_givenNullId_thenThrowsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> new Transaction(null, TYPE, AMOUNT, PARENT_ID));
    }

    @Test
    void createTransaction_givenNullType_thenThrowsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> new Transaction(ID, null, AMOUNT, PARENT_ID));
    }

    @Test
    void createTransaction_givenNullAmount_thenThrowsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> new Transaction(ID, TYPE, null, PARENT_ID));
    }

    @Test
    void createTransaction_givenParentIdEqualToId_thenThrowsInvalidTransactionException() {
        assertThrows(InvalidTransactionException.class, () -> new Transaction(ID, TYPE, AMOUNT, ID));
    }
}
