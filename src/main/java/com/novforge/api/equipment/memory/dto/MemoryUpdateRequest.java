package com.novforge.api.equipment.memory.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MemoryUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 255) String name,
        @PositiveOrZero Long price,
        @Size(min = 1, max = 20) String type,
        @Positive Integer capacity,
        @Positive Integer clock,
        @Positive Integer moduleCount,
        @Positive Integer moduleCapacity,
        @Size(min = 1, max = 20) String formFactor,
        @Positive Integer casLatency,
        @DecimalMin("0.01") @DecimalMax("9.99") @Digits(integer = 1, fraction = 2) BigDecimal voltage,
        Boolean ecc,
        String description,
        String imageUrl
) {
}
