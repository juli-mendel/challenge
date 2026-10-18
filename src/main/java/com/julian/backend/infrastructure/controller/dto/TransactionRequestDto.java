package com.julian.backend.infrastructure.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record TransactionRequestDto(@NotNull Double amount, @NotNull String type, @JsonProperty("parent_id") Long parentId) { }
