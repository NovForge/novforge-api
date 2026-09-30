package com.novforge.api.equipment.storage.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record StorageUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 255) String name,
        @PositiveOrZero Long price,
        @Size(min = 1, max = 20) String type,
        @Size(min = 1, max = 30) String interfaceType,
        @Positive Integer capacity,
        @PositiveOrZero Integer readSpeed,
        @Size(min = 1, max = 20) String formFactor,
        @PositiveOrZero Integer cacheSize,
        String description,
        String imageUrl
) {
}
