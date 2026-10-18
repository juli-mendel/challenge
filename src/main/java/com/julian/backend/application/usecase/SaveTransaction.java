package com.julian.backend.application.usecase;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.exception.TransactionCycleException;
import com.julian.backend.domain.model.Transaction;

public interface SaveTransaction {
    /**
     * Creates the transaction or replaces the existing one with the same id.
     *
     * @param transaction the transaction to store
     * @throws ParentTransactionNotFoundException if the parent id does not exist
     * @throws TransactionCycleException if the new parent is the transaction itself or one of its descendants
     */
    void saveTransaction(Transaction transaction);
}
