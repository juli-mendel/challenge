package com.julian.backend.application.usecase;

import com.julian.backend.domain.model.Transaction;

import java.util.List;

public interface GetTransactionsByType {
    List<Transaction> getTransactionsByType(String type);
}
