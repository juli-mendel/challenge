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
        double previousAmount = getPreviousAmount(transaction);
        double amountDelta = transaction.getAmount() - previousAmount;

        transactionById.put(transaction.getId(), transaction);
        sumByTransactionId.merge(transaction.getId(), amountDelta, Double::sum);
        addAmountToAncestors(transaction, amountDelta);
    }

    private Double getPreviousAmount(Transaction transaction) {
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
}
