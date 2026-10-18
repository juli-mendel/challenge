package com.julian.backend.infrastructure.controller;

import com.julian.backend.application.usecase.GetTransactionIdsByType;
import com.julian.backend.application.usecase.GetTransactionSum;
import com.julian.backend.application.usecase.SaveTransaction;
import com.julian.backend.domain.model.Transaction;
import com.julian.backend.infrastructure.controller.dto.StatusResponseDto;
import com.julian.backend.infrastructure.controller.dto.SumResponseDto;
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
    public ResponseEntity<SumResponseDto> getTransactionSum(@PathVariable @PositiveOrZero Long id) {
        double transactionSum = getTransactionSum.getTransactionSum(id);
        return ResponseEntity.ok(new SumResponseDto(transactionSum));
    }

    // PUT /transaction/123 body: {"amount": "1000", "type":"car"} -> {"status":"ok"}
    @PutMapping("transactions/{id}")
    public ResponseEntity<StatusResponseDto> saveTransaction(@PathVariable @PositiveOrZero Long id,
                                                @RequestBody @Valid TransactionRequestDto transactionDto) {
        Transaction transaction = TransactionMapper.toTransaction(id, transactionDto);
        saveTransaction.saveTransaction(transaction);
        StatusResponseDto statusResponseDto = new StatusResponseDto("ok");
        return ResponseEntity.ok(statusResponseDto);
    }
}
