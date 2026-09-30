package com.novforge.api.equipment.pccase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PcCaseCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Long price,
        @NotBlank @Size(max = 30) String type,
        @NotBlank @Size(max = 100) String supportedFormFactor,
        @NotNull @Positive Integer maxGpuLength,
        @NotNull @Positive Integer maxCpuCoolerHeight,
        @NotBlank @Size(max = 100) String supportedRadiatorSize,
        @NotNull @PositiveOrZero Integer fanCount,
        String description,
        String imageUrl
) {
}
