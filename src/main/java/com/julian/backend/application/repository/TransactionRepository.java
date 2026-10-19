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
     * Returns the stored sum for the transaction.
     *
     * @param id the transaction id
     * @return the stored sum, or empty if none was stored
     */
    Optional<Double> getTransactionSumById(Long id);

    /**
     * Stores the sum of the transaction.
     *
     * @param id the transaction id
     * @param sum the sum to store
     */
    void putTransactionSum(Long id, double sum);

    /**
     * Adds the amount to the stored sum of the transaction.
     *
     * @param id the transaction id
     * @param amount the amount to add
     */
    void addToTransactionSum(Long id, double amount);

    /**
     * Returns the transactions ids with exactly the given type.
     *
     * @param type the transaction type
     * @return the transactions ids with that type, or an empty list if there are none
     */
    List<Long> getTransactionIdsByType(String type);
}
