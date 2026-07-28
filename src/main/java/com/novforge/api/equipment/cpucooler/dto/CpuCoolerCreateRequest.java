package com.novforge.api.equipment.cpucooler.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CpuCoolerCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Long price,
        @NotBlank @Size(max = 20) String type,
        @NotBlank @Size(max = 100) String socket,
        @NotNull @Positive Integer fanSize,
        @NotNull @PositiveOrZero Integer radiatorSize,
        @NotNull @Positive Integer height,
        @NotNull @DecimalMin("0.00") @DecimalMax("999.99") @Digits(integer = 3, fraction = 2) BigDecimal airflow,
        @NotNull @DecimalMin("0.0") @DecimalMax("999.9") @Digits(integer = 3, fraction = 1) BigDecimal noiseLevel,
        @NotNull Boolean rgb,
        String description,
        String imageUrl
) {
}
