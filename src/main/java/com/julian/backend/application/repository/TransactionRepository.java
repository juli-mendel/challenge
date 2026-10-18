package com.julian.backend.application.repository;

import com.julian.backend.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    /**
     * Creates the transaction or replaces the existing one with the same id.
     *
     * @param transaction the transaction to store
     */
    void save(Transaction transaction);

    /**
     * Checks whether a transaction with the given id is stored.
     *
     * @param id the transaction id
     * @return true if a transaction with that id exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Finds a transaction by its id.
     *
     * @param id the transaction id
     * @return the transaction, or empty if no transaction has that id
     */
    Optional<Transaction> findById(Long id);

    /**
     * Returns the amount of the transaction plus the amounts of all its
     * descendants, transitively linked by parentId.
     *
     * @param id the transaction id
     * @return the sum, or empty if no transaction has that id
     */
    Optional<Double> getTransactionSumById(Long id);

    /**
     * Returns the transactions with exactly the given type.
     *
     * @param type the transaction type
     * @return the transactions with that type, or an empty list if there are none
     */
    List<Transaction> getTransactionsByType(String type);
}
