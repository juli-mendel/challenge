package com.julian.backend.application.usecase;

import com.julian.backend.domain.model.Transaction;

public interface SaveTransaction {
    void saveTransaction(Transaction transaction);
}
