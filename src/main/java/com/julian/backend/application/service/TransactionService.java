package com.julian.backend.application.service;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.exception.TransactionCycleException;
import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.application.usecase.GetTransactionSum;
import com.julian.backend.application.usecase.GetTransactionIdsByType;
import com.julian.backend.application.usecase.SaveTransaction;
import com.julian.backend.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements SaveTransaction, GetTransactionSum, GetTransactionIdsByType {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public synchronized void saveTransaction(Transaction transaction) {
        verifyParentId(transaction);
        verifyNoCycle(transaction);

        Optional<Transaction> previousTransaction = transactionRepository.findById(transaction.getId());
        double childrenSum = previousTransaction
            .map(existingTransaction -> storedSum(existingTransaction.getId()) - existingTransaction.getAmount())
            .orElse(0.0);
        previousTransaction.ifPresent(existingTransaction -> addAmountToAncestors(existingTransaction, -storedSum(existingTransaction.getId())));

        transactionRepository.save(transaction);
        double sum = childrenSum + transaction.getAmount();
        transactionRepository.putTransactionSum(transaction.getId(), sum);
        addAmountToAncestors(transaction, sum);
    }

    private double storedSum(Long id) {
        return transactionRepository.getTransactionSumById(id).orElseThrow();
    }

    private void addAmountToAncestors(Transaction transaction, double amount) {
        Optional<Long> ancestorId = transaction.getParentId();
        while (ancestorId.isPresent()) {
            transactionRepository.addToTransactionSum(ancestorId.get(), amount);
            ancestorId = transactionRepository.findById(ancestorId.get()).flatMap(Transaction::getParentId);
        }
    }

    private void verifyNoCycle(Transaction transaction) {
        Optional<Long> ancestorId = transaction.getParentId();
        while (ancestorId.isPresent()) {
            boolean isCycleDetected = ancestorId.get().equals(transaction.getId());
            if (isCycleDetected) {
                throw new TransactionCycleException(transaction.getId(), transaction.getParentId().get());
            }
            ancestorId = transactionRepository.findById(ancestorId.get()).flatMap(Transaction::getParentId);
        }
    }

    private void verifyParentId(Transaction transaction) {
        transaction.getParentId().ifPresent(parentId -> {
            if (!transactionRepository.existsById(parentId)) {
                throw new ParentTransactionNotFoundException(parentId);
            }
        });
    }

    @Override
    public synchronized double getTransactionSum(Long id) {
        return transactionRepository.getTransactionSumById(id)
            .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Override
    public synchronized List<Long> getTransactionIdsByType(String type) {
        return transactionRepository.getTransactionIdsByType(type);
    }
}
