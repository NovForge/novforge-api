package com.novforge.api.equipment.gpu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record GpuCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Long price,
        @NotNull @Positive Integer memorySize,
        @NotBlank @Size(max = 20) String memoryType,
        @NotNull @Positive Integer length,
        @NotNull @PositiveOrZero Integer powerConsumption,
        @NotNull @Positive Integer recommendedPsu,
        String description,
        String imageUrl
) {
}
