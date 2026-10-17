package com.julian.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    void givenNewTransactionWithId_whenGetId_thenReturnsId() {
        Transaction transaction = new Transaction(10L);

        assertEquals(10L, transaction.getId());
    }
}
