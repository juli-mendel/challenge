package com.julian.backend.infrastructure.controller.handler;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
