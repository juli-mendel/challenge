package com.julian.backend.infrastructure.controller.mapper;

import com.julian.backend.domain.model.Transaction;
import com.julian.backend.infrastructure.controller.dto.TransactionRequestDto;

public final class TransactionMapper {

    private TransactionMapper() {}

    public static Transaction toTransaction(Long id, TransactionRequestDto transactionRequestDto) {
        return new Transaction(id, transactionRequestDto.type(), transactionRequestDto.amount(), transactionRequestDto.parentId());
    }
}
