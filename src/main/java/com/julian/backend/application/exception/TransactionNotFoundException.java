package com.julian.backend.application.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(Long id) {
        super("transaction not found with id: " + id);
    }
}
