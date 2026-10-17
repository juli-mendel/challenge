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
    Map<Long, Double> sumByTransactionId;

    public InMemoryTransactionRepository() {
        this.transactionById = new HashMap<>();
        this.sumByTransactionId = new HashMap<>();
    }

    @Override
    public void save(Transaction transaction) {
        transactionById.put(transaction.getId(), transaction);
        sumByTransactionId.put(transaction.getId(), transaction.getAmount());
        addAmountToAncestors(transaction);
    }

    private void addAmountToAncestors(Transaction transaction) {
        Optional<Long> ancestorId = transaction.getParentId();
        while (ancestorId.isPresent()) {
            sumByTransactionId.merge(ancestorId.get(), transaction.getAmount(), Double::sum);
            ancestorId = findById(ancestorId.get()).flatMap(Transaction::getParentId);
        }
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
}
