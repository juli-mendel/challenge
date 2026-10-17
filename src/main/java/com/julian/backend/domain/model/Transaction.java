package com.julian.backend.domain.model;

public class Transaction {

    private final Long id;
    private final String type;
    private final Double amount;

    public Transaction(Long id, String type, Double amount) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null");
        }
        this.id = id;
        this.type = type;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }
}
