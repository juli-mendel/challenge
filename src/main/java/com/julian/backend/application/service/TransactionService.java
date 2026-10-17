package com.julian.backend.application.service;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.exception.TransactionCycleException;
import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.application.usecase.GetTransactionSum;
import com.julian.backend.application.usecase.GetTransactionsByType;
import com.julian.backend.application.usecase.SaveTransaction;
import com.julian.backend.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements SaveTransaction, GetTransactionSum, GetTransactionsByType {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        verifyParentId(transaction);
        verifyNoCycle(transaction);
        transactionRepository.save(transaction);
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
    public double getTransactionSum(Long id) {
        return transactionRepository.getTransactionSumById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Override
    public List<Transaction> getTransactionsByType(String type) {
        return List.of();
    }
}
