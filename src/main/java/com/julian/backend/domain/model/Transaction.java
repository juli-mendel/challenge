package com.julian.backend.domain.model;

public class Transaction {

    private final Long id;
    private final String type;
    private final Double amount;

    public Transaction(Long id, String type, Double amount) {
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
