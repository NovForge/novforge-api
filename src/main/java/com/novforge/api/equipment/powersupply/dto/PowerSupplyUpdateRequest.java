package com.novforge.api.equipment.powersupply.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PowerSupplyUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 255) String name,
        @PositiveOrZero Long price,
        @Positive Integer wattage,
        @Size(min = 1, max = 30) String efficiency,
        @Size(min = 1, max = 30) String modularType,
        @Size(min = 1, max = 20) String formFactor,
        String description,
        String imageUrl
) {
}
