package com.novforge.api.equipment.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record StorageCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Long price,
        @NotBlank @Size(max = 20) String type,
        @NotBlank @Size(max = 30) String interfaceType,
        @NotNull @Positive Integer capacity,
        @NotNull @PositiveOrZero Integer readSpeed,
        @NotBlank @Size(max = 20) String formFactor,
        @NotNull @PositiveOrZero Integer cacheSize,
        String description,
        String imageUrl
) {
}
