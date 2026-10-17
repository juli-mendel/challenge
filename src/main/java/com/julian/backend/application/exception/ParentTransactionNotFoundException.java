package com.julian.backend.application.exception;

public class ParentTransactionNotFoundException extends RuntimeException {

    public ParentTransactionNotFoundException(Long parentId) {
        super("parent transaction " + parentId + " does not exist");
    }
}
