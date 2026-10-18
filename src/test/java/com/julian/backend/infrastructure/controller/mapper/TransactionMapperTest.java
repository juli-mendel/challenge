package com.julian.backend.infrastructure.controller.mapper;

import com.julian.backend.domain.model.Transaction;
import com.julian.backend.infrastructure.controller.dto.TransactionRequestDto;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionMapperTest {
    private static final Long ID = 10L;
    private static final String TYPE = "cars";
    private static final Double AMOUNT = 5000.0;
    private static final Long PARENT_ID = 5L;

    @Test
    void toTransaction_givenRequestWithParent_thenMapsAllFields() {
        TransactionRequestDto request = new TransactionRequestDto(AMOUNT, TYPE, PARENT_ID);

        Transaction transaction = TransactionMapper.toTransaction(ID, request);

        assertEquals(ID, transaction.getId());
        assertEquals(TYPE, transaction.getType());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals(Optional.of(PARENT_ID), transaction.getParentId());
    }

    @Test
    void toTransaction_givenRequestWithoutParent_thenParentIdIsEmpty() {
        TransactionRequestDto request = new TransactionRequestDto(AMOUNT, TYPE, null);

        Transaction transaction = TransactionMapper.toTransaction(ID, request);

        assertEquals(Optional.empty(), transaction.getParentId());
    }
}
