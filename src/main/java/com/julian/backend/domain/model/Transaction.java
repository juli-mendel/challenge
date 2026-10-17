package com.julian.backend.domain.model;

public class Transaction {

    private final Long id;
    public Transaction(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
