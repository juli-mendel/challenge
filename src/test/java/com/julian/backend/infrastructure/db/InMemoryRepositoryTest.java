package com.julian.backend.infrastructure.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.julian.backend.domain.model.Transaction;

public class InMemoryRepositoryTest {

    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;

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
}
