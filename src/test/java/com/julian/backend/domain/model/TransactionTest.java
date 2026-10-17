package com.julian.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    void givenNewTransactionWithId_whenGetId_thenReturnsId() {
        Transaction transaction = new Transaction(10L, "cars");

        assertEquals(10L, transaction.getId());
    }

    @Test
    void givenNewTransactionWithType_whenGetType_thenReturnsType() {
        Transaction transaction = new Transaction(10L, "cars");

        assertEquals("cars", transaction.getType());
    }
}
