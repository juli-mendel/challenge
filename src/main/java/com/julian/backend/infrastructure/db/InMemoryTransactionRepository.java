package com.julian.backend.infrastructure.db;

import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.domain.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    Map<Long, Transaction> transactionById;

    public InMemoryTransactionRepository() {
        this.transactionById = new HashMap<>();
    }

    @Override
    public void save(Transaction transaction) {
        transactionById.put(transaction.getId(), transaction);
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(transactionById.get(id));
    }
}
