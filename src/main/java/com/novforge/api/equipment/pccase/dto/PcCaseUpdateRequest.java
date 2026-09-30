package com.novforge.api.equipment.pccase.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PcCaseUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 255) String name,
        @PositiveOrZero Long price,
        @Size(min = 1, max = 30) String type,
        @Size(min = 1, max = 100) String supportedFormFactor,
        @Positive Integer maxGpuLength,
        @Positive Integer maxCpuCoolerHeight,
        @Size(min = 1, max = 100) String supportedRadiatorSize,
        @PositiveOrZero Integer fanCount,
        String description,
        String imageUrl
) {
}
