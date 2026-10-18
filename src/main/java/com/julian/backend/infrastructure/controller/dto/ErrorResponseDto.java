package com.julian.backend.infrastructure.controller.dto;

public record ErrorResponseDto(String message, String errorDetail, String status) {
}
