package com.julian.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    void givenNewTransactionWithId_whenGetId_thenReturnsId() {
        Transaction transaction = new Transaction(10L, "cars", 5000.0);

        assertEquals(10L, transaction.getId());
    }

    @Test
    void givenNewTransactionWithType_whenGetType_thenReturnsType() {
        Transaction transaction = new Transaction(10L, "cars", 5000.0);

        assertEquals("cars", transaction.getType());
    }

    @Test
    void givenNewTransactionWithAmount_whenGetAmount_thenReturnsAmount() {
        Transaction transaction = new Transaction(10L, "cars", 5000.0);

        assertEquals(5000.0, transaction.getAmount());
    }
}
