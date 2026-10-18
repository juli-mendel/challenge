package com.julian.backend.application.usecase;

import java.util.List;

public interface GetTransactionIdsByType {
    /**
     * Returns the transaction ids with exactly the given type.
     *
     * @param type the transaction type
     * @return the transaction ids with that type, or an empty list if there are none
     */
    List<Long> getTransactionIdsByType(String type);
}
