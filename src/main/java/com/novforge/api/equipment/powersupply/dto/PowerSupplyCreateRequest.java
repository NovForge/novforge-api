package com.novforge.api.equipment.powersupply.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PowerSupplyCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Long price,
        @NotNull @Positive Integer wattage,
        @NotBlank @Size(max = 30) String efficiency,
        @NotBlank @Size(max = 30) String modularType,
        @NotBlank @Size(max = 20) String formFactor,
        String description,
        String imageUrl
) {
}
