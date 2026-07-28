package com.novforge.api.equipment.cpu.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CpuCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 100) String name,
        @NotNull @PositiveOrZero Long price,
        @NotBlank @Size(max = 30) String socket,
        @NotNull @Positive Integer cores,
        @NotNull @Positive Integer threads,
        @NotNull @DecimalMin("0.01") @DecimalMax("9.99") @Digits(integer = 1, fraction = 2) BigDecimal baseClock,
        @NotNull @DecimalMin("0.01") @DecimalMax("9.99") @Digits(integer = 1, fraction = 2) BigDecimal boostClock,
        @NotBlank @Size(max = 30) String cache,
        @NotNull @PositiveOrZero Integer tdp,
        @NotNull Boolean integratedGraphics,
        @NotBlank @Size(max = 100) String memorySupport,
        @NotBlank @Size(max = 20) String pcieVersion,
        String description,
        String imageUrl
) {
}
