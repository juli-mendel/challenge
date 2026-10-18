package com.julian.backend.infrastructure.controller;

import com.julian.backend.application.usecase.GetTransactionIdsByType;
import com.julian.backend.application.usecase.GetTransactionSum;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {

    private final GetTransactionIdsByType getTransactionIdsByType;
    private final GetTransactionSum getTransactionSum;

    public TransactionController(GetTransactionIdsByType getTransactionIdsByType,
                                 GetTransactionSum getTransactionSum
                                 ) {
        this.getTransactionIdsByType = getTransactionIdsByType;
        this.getTransactionSum = getTransactionSum;
    }

    // GET /transaction/type/car -> {[1, 34, 100]}
    @GetMapping("transactions/type/{type}")
    public ResponseEntity<List<Long>> getTransactionIdsByType(@PathVariable String type) {
        return ResponseEntity.ok(getTransactionIdsByType.getTransactionIdsByType(type));
    }

    // GET /transaction/sum/123 -> 123.0
    @GetMapping("transactions/sum/{id}")
    public ResponseEntity<Double> getTransactionSum(@PathVariable @PositiveOrZero Long id) {
        return ResponseEntity.ok(getTransactionSum.getTransactionSum(id));
    }

    @PutMapping("transactions/{id}")
    public ResponseEntity<Void> saveTransaction(@PathVariable @PositiveOrZero Long id) {
        return ResponseEntity.ok().build();
    }
}
