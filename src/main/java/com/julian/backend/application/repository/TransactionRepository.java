package com.julian.backend.application.repository;

import com.julian.backend.domain.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction transaction);

    boolean existsById(Long id);

    Optional<Transaction> findById(Long id);
}
