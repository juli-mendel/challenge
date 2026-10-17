package com.julian.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class TransactionTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;
    private static final Long PARENT_ID = 5L;

    @Test
    void givenNewTransactionWithId_whenGetId_thenReturnsId() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(ID, transaction.getId());
    }

    @Test
    void givenNewTransactionWithType_whenGetType_thenReturnsType() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(TYPE, transaction.getType());
    }

    @Test
    void givenNewTransactionWithEmptyType_whenGetType_thenReturnsEmptyType() {
        Transaction transaction = new Transaction(ID, "", AMOUNT, PARENT_ID);

        assertEquals("", transaction.getType());
    }

    @Test
    void givenNewTransactionWithAmount_whenGetAmount_thenReturnsAmount() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(AMOUNT, transaction.getAmount());
    }

    @Test
    void givenNewTransactionWithParentId_whenGetParentId_thenReturnsParentId() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, PARENT_ID);

        assertEquals(Optional.of(PARENT_ID), transaction.getParentId());
    }

    @Test
    void givenNewTransactionWithoutParentId_whenGetParentId_thenReturnsEmpty() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT, null);

        assertEquals(Optional.empty(), transaction.getParentId());
    }

    @Test
    void givenNullId_whenCreateTransaction_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(null, TYPE, AMOUNT, PARENT_ID));
    }

    @Test
    void givenNullType_whenCreateTransaction_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(ID, null, AMOUNT, PARENT_ID));
    }

    @Test
    void givenNullAmount_whenCreateTransaction_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(ID, TYPE, null, PARENT_ID));
    }
}
