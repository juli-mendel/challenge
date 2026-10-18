package com.julian.backend.domain.model;

import com.julian.backend.domain.exception.InvalidTransactionException;

import java.util.Optional;

public class Transaction {

    private final Long id;
    private final String type;
    private final Double amount;
    private final Long parentId;

    public Transaction(Long id, String type, Double amount, Long parentId) {
        if (id == null) {
            throw new InvalidTransactionException("id must not be null");
        }
        if (id < 0) {
            throw new InvalidTransactionException("id must not be negative");
        }
        if (type == null) {
            throw new InvalidTransactionException("type must not be null");
        }
        if (amount == null) {
            throw new InvalidTransactionException("amount must not be null");
        }
        if (id.equals(parentId)) {
            throw new InvalidTransactionException("parentId must not be equal to id");
        }
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.parentId = parentId;
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

    public Optional<Long> getParentId() {
        return Optional.ofNullable(parentId);
    }
}
