package com.julian.backend.infrastructure.controller;

import com.julian.backend.application.usecase.GetTransactionIdsByType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Long>> getTransactionIdsByType(@PathVariable String type) {
        return ResponseEntity.ok(getTransactionIdsByType.getTransactionIdsByType(type));
    }
}
