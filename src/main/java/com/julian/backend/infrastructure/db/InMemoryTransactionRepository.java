package com.julian.backend.infrastructure.db;

import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.domain.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<Long, Transaction> transactionById;
    private final Map<Long, Double> sumByTransactionId;
    private final Map<String, Set<Long>> transactionIdsByType;

    public InMemoryTransactionRepository() {
        this.transactionById = new HashMap<>();
        this.sumByTransactionId = new HashMap<>();
        this.transactionIdsByType = new HashMap<>();
    }

    @Override
    public void save(Transaction transaction) {
        findById(transaction.getId()).ifPresent(previous ->
                transactionIdsByType.get(previous.getType()).remove(previous.getId()));
        transactionById.put(transaction.getId(), transaction);
        transactionIdsByType.computeIfAbsent(transaction.getType(), type -> new LinkedHashSet<>())
                .add(transaction.getId());
    }

    @Override
    public boolean existsById(Long id) {
        return transactionById.containsKey(id);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(transactionById.get(id));
    }

    @Override
    public Optional<Double> getTransactionSumById(Long id) {
        return Optional.ofNullable(sumByTransactionId.get(id));
    }

    @Override
    public void putTransactionSum(Long id, double sum) {
        sumByTransactionId.put(id, sum);
    }

    @Override
    public void addToTransactionSum(Long id, double amount) {
        sumByTransactionId.merge(id, amount, Double::sum);
    }

    @Override
    public List<Long> getTransactionIdsByType(String type) {
        return transactionIdsByType.getOrDefault(type, Set.of()).stream().toList();
    }
}
