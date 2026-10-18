package com.julian.backend.infrastructure.controller;

import com.julian.backend.application.usecase.GetTransactionIdsByType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {

    private final GetTransactionIdsByType getTransactionIdsByType;

    public TransactionController(GetTransactionIdsByType getTransactionIdsByType) {
        this.getTransactionIdsByType = getTransactionIdsByType;
    }

    // GET /transaction/type/car -> {[1, 34, 100]}
    @GetMapping("transactions/type/{type}")
    public List<Long> getTransactionIdsByType(@PathVariable String type) {
        return List.of();
    }
}
