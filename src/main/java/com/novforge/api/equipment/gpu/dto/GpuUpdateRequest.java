package com.novforge.api.equipment.gpu.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record GpuUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 255) String name,
        @PositiveOrZero Long price,
        @Positive Integer memorySize,
        @Size(min = 1, max = 20) String memoryType,
        @Positive Integer length,
        @PositiveOrZero Integer powerConsumption,
        @Positive Integer recommendedPsu,
        String description,
        String imageUrl
) {
}
