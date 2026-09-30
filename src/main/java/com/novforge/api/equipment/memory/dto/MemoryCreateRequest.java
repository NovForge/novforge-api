package com.novforge.api.equipment.memory.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MemoryCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Long price,
        @NotBlank @Size(max = 20) String type,
        @NotNull @Positive Integer capacity,
        @NotNull @Positive Integer clock,
        @NotNull @Positive Integer moduleCount,
        @NotNull @Positive Integer moduleCapacity,
        @NotBlank @Size(max = 20) String formFactor,
        @NotNull @Positive Integer casLatency,
        @NotNull @DecimalMin("0.01") @DecimalMax("9.99") @Digits(integer = 1, fraction = 2) BigDecimal voltage,
        @NotNull Boolean ecc,
        String description,
        String imageUrl
) {
}
