package com.julian.backend.infrastructure.controller;

import com.julian.backend.application.usecase.GetTransactionIdsByType;
import com.julian.backend.application.usecase.GetTransactionSum;
import com.julian.backend.application.usecase.SaveTransaction;
import com.julian.backend.domain.model.Transaction;
import com.julian.backend.infrastructure.controller.dto.TransactionRequestDto;
import com.julian.backend.infrastructure.controller.mapper.TransactionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    private final GetTransactionIdsByType getTransactionIdsByType;
    private final GetTransactionSum getTransactionSum;
    private final SaveTransaction saveTransaction;

    public TransactionController(GetTransactionIdsByType getTransactionIdsByType,
                                 GetTransactionSum getTransactionSum,
                                 SaveTransaction saveTransaction
                                 ) {
        this.getTransactionIdsByType = getTransactionIdsByType;
        this.getTransactionSum = getTransactionSum;
        this.saveTransaction =  saveTransaction;
    }

    // GET /transaction/type/car -> {[1, 34, 100]}
    @GetMapping("transactions/types/{type}")
    public ResponseEntity<List<Long>> getTransactionIdsByType(@PathVariable String type) {
        return ResponseEntity.ok(getTransactionIdsByType.getTransactionIdsByType(type));
    }

    // GET /transaction/sum/123 -> 123.0
    @GetMapping("transactions/sum/{id}")
    public ResponseEntity<Double> getTransactionSum(@PathVariable @PositiveOrZero Long id) {
        return ResponseEntity.ok(getTransactionSum.getTransactionSum(id));
    }

    @PutMapping("transactions/{id}")
    public ResponseEntity<Void> saveTransaction(@PathVariable @PositiveOrZero Long id,
                                                @RequestBody @Valid TransactionRequestDto transactionDto) {
        Transaction transaction = TransactionMapper.toTransaction(id, transactionDto);
        saveTransaction.saveTransaction(transaction);
        return ResponseEntity.ok().build();
    }
}
