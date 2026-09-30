package com.novforge.api.equipment.cpucooler.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CpuCoolerUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 255) String name,
        @PositiveOrZero Long price,
        @Size(min = 1, max = 20) String type,
        @Size(min = 1, max = 100) String socket,
        @Positive Integer fanSize,
        @PositiveOrZero Integer radiatorSize,
        @Positive Integer height,
        @DecimalMin("0.00") @DecimalMax("999.99") @Digits(integer = 3, fraction = 2) BigDecimal airflow,
        @DecimalMin("0.0") @DecimalMax("999.9") @Digits(integer = 3, fraction = 1) BigDecimal noiseLevel,
        Boolean rgb,
        String description,
        String imageUrl
) {
}
