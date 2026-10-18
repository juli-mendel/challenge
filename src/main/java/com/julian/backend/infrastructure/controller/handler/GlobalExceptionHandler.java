package com.julian.backend.infrastructure.controller.handler;

import com.julian.backend.application.exception.ParentTransactionNotFoundException;
import com.julian.backend.application.exception.TransactionCycleException;
import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.domain.exception.InvalidTransactionException;
import com.julian.backend.infrastructure.controller.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(TransactionNotFoundException ex) {
        int errorCode = 404;
        ErrorResponseDto responseDto = new ErrorResponseDto(
            "Not Found Error",
            ex.getMessage(),
            String.valueOf(errorCode)
        );
        return ResponseEntity.status(errorCode).body(responseDto);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(HandlerMethodValidationException ex) {
        int errorCode = 400;
        ErrorResponseDto responseDto = new ErrorResponseDto(
            "Bad Request Error",
            ex.getMessage(),
            String.valueOf(errorCode)
        );
        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTransactionException(InvalidTransactionException ex) {
        int errorCode = 400;
        ErrorResponseDto responseDto = new ErrorResponseDto(
            "Bad Request Error",
            ex.getMessage(),
            String.valueOf(errorCode)
        );
        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(ParentTransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleParentTransactionNotFoundException(ParentTransactionNotFoundException ex) {
        int errorCode = 400;
        ErrorResponseDto responseDto = new ErrorResponseDto(
            "Bad Request Error",
            ex.getMessage(),
            String.valueOf(errorCode)
        );
        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(TransactionCycleException.class)
    public ResponseEntity<ErrorResponseDto> handleTransactionCycleException(TransactionCycleException ex) {
        int errorCode = 400;
        ErrorResponseDto responseDto = new ErrorResponseDto(
            "Bad Request Error",
            ex.getMessage(),
            String.valueOf(errorCode)
        );
        return ResponseEntity.badRequest().body(responseDto);
    }
}
