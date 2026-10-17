package com.julian.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TransactionTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;

    @Test
    void givenNewTransactionWithId_whenGetId_thenReturnsId() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT);

        assertEquals(ID, transaction.getId());
    }

    @Test
    void givenNewTransactionWithType_whenGetType_thenReturnsType() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT);

        assertEquals(TYPE, transaction.getType());
    }

    @Test
    void givenNewTransactionWithAmount_whenGetAmount_thenReturnsAmount() {
        Transaction transaction = new Transaction(ID, TYPE, AMOUNT);

        assertEquals(AMOUNT, transaction.getAmount());
    }

    @Test
    void givenNullId_whenCreateTransaction_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(null, TYPE, AMOUNT));
    }

    @Test
    void givenNullType_whenCreateTransaction_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(ID, null, AMOUNT));
    }

    @Test
    void givenNullAmount_whenCreateTransaction_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(ID, TYPE, null));
    }
}
