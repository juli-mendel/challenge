package com.julian.backend.application.exception;

public class TransactionCycleException extends RuntimeException {

    public TransactionCycleException(Long transactionId, Long parentId) {
        super("setting parent " + parentId + " on transaction " + transactionId + " creates a cycle");
    }
}
