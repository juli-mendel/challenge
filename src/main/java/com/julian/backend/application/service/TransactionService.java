package com.julian.backend.application.service;

import com.julian.backend.application.repository.TransactionRepository;
import com.julian.backend.application.usecase.SaveTransaction;
import com.julian.backend.domain.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements SaveTransaction {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        verifyParentId(transaction);
        transactionRepository.save(transaction);
    }

    private void verifyParentId(Transaction transaction) {
        transaction.getParentId().ifPresent(parentId -> {
            if (!transactionRepository.existsById(parentId)) {
                throw new IllegalArgumentException("parent transaction " + parentId + " does not exist");
            }
        });
    }
}
