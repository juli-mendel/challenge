package com.julian.backend.infrastructure.db;

import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.domain.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    Map<Long, Transaction> transactionById;
    Map<Long, Double> sumByTransactionId;
    Map<String, List<Transaction>> transactionsByType;

    public InMemoryTransactionRepository() {
        this.transactionById = new HashMap<>();
        this.sumByTransactionId = new HashMap<>();
        this.transactionsByType = new HashMap<>();
    }

    @Override
    public void save(Transaction transaction) {
        double previousSum = getPreviousSum(transaction);
        double previousAmount = getPreviousAmount(transaction);
        double newAmount = transaction.getAmount();
        double newSum = previousSum - previousAmount + newAmount;

        substractPreviousSumFromAncestors(transaction, previousSum);
        transactionById.put(transaction.getId(), transaction);
        sumByTransactionId.put(transaction.getId(), newSum);
        addAmountToAncestors(transaction, newSum);

        addInTransactionByType(transaction);
    }

    private void addInTransactionByType(Transaction transaction) {
        transactionsByType.computeIfAbsent(transaction.getType(), type -> new ArrayList<>()).add(transaction);
    }

    private void substractPreviousSumFromAncestors(Transaction transaction, double previousSum) {
        findById(transaction.getId()).ifPresent(previousTransaction -> addAmountToAncestors(previousTransaction, -previousSum));
    }

    private Double getPreviousSum(Transaction transaction) {
        return getTransactionSumById(transaction.getId()).orElse(0.0);
    }

    private double getPreviousAmount(Transaction transaction) {
        return findById(transaction.getId()).map(Transaction::getAmount).orElse(0.0);
    }

    private void addAmountToAncestors(Transaction transaction, double amount) {
        Optional<Long> ancestorId = transaction.getParentId();
        while (ancestorId.isPresent()) {
            sumByTransactionId.merge(ancestorId.get(), amount, Double::sum);
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

    @Override
    public List<Transaction> getTransactionsByType(String type) {
        return List.copyOf(transactionsByType.getOrDefault(type, List.of()));
    }
}
