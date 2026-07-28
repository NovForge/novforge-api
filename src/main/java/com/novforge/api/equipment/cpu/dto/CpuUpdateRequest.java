package com.novforge.api.equipment.cpu.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CpuUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 100) String name,
        @PositiveOrZero Long price,
        @Size(min = 1, max = 30) String socket,
        @Positive Integer cores,
        @Positive Integer threads,
        @DecimalMin("0.01") @DecimalMax("9.99") @Digits(integer = 1, fraction = 2) BigDecimal baseClock,
        @DecimalMin("0.01") @DecimalMax("9.99") @Digits(integer = 1, fraction = 2) BigDecimal boostClock,
        @Size(min = 1, max = 30) String cache,
        @PositiveOrZero Integer tdp,
        Boolean integratedGraphics,
        @Size(min = 1, max = 100) String memorySupport,
        @Size(min = 1, max = 20) String pcieVersion,
        String description,
        String imageUrl
) {
}
