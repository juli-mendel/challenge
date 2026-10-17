package com.julian.backend.application.repository;

import com.julian.backend.domain.model.Transaction;

public interface TransactionRepository {
    void save(Transaction transaction);

    boolean existsById(Long parentId);
}
