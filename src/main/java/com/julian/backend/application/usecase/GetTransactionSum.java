package com.julian.backend.application.usecase;

import com.julian.backend.application.exception.TransactionNotFoundException;

public interface GetTransactionSum {
    /**
     * Returns the amount of the transaction plus the amount of all its
     * descendants, transitively linked by parentId.
     *
     * @param id the transaction id
     * @return the sum
     * @throws TransactionNotFoundException if no transaction has that id
     */
    double getTransactionSum(Long id);
}
