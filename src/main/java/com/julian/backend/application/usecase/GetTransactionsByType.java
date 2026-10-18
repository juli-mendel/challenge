package com.julian.backend.application.usecase;

import com.julian.backend.domain.model.Transaction;

import java.util.List;

public interface GetTransactionsByType {
    /**
     * Returns the transactions with exactly the given type.
     *
     * @param type the transaction type
     * @return the transactions with that type, or an empty list if there are none
     */
    List<Transaction> getTransactionsByType(String type);
}
