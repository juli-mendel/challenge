package com.julian.backend.application.repository;

import com.julian.backend.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction transaction);

    boolean existsById(Long id);

    Optional<Transaction> findById(Long id);

    Optional<Double> getTransactionSumById(Long id);

    List<Transaction> getTransactionsByType(String type);
}
