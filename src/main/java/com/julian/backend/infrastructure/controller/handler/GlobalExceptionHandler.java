package com.julian.backend.infrastructure.controller.handler;

import com.julian.backend.application.exception.TransactionNotFoundException;
import com.julian.backend.infrastructure.controller.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
