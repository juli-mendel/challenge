package com.julian.backend.domain.model;

import java.util.Optional;

public class Transaction {

    private final Long id;
    private final String type;
    private final Double amount;
    private final Long parentId;

    public Transaction(Long id, String type, Double amount, Long parentId) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null");
        }
        if (id.equals(parentId)) {
            throw new IllegalArgumentException("parentId must not be equal to id");
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
