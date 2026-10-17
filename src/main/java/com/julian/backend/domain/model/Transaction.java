package com.julian.backend.domain.model;

public class Transaction {

    private final Long id;
    private final String type;

    public Transaction(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
