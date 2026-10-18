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
        Optional<Transaction> previous = findById(transaction.getId());
        double childrenSum = previous.map(this::getChildrenSum).orElse(0.0);

        previous.ifPresent(this::removePreviousTransactionFromIndexes);
        addNewTransactionToIndexes(transaction, childrenSum + transaction.getAmount());
    }

    private double getChildrenSum(Transaction transaction) {
        return sumByTransactionId.get(transaction.getId()) - transaction.getAmount();
    }

    private void removePreviousTransactionFromIndexes(Transaction previous) {
        addAmountToAncestors(previous, -sumByTransactionId.get(previous.getId()));
        transactionIdsByType.get(previous.getType()).remove(previous.getId());
    }

    private void addNewTransactionToIndexes(Transaction transaction, double sum) {
        transactionById.put(transaction.getId(), transaction);
        sumByTransactionId.put(transaction.getId(), sum);
        addAmountToAncestors(transaction, sum);
        transactionIdsByType.computeIfAbsent(transaction.getType(), type -> new LinkedHashSet<>())
                .add(transaction.getId());
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
    public List<Long> getTransactionIdsByType(String type) {
        return transactionIdsByType.getOrDefault(type, Set.of()).stream().toList();
    }
}
