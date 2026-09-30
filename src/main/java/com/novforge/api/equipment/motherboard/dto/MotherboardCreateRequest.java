package com.novforge.api.equipment.motherboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MotherboardCreateRequest(
        @NotBlank @Size(max = 100) String manufacturer,
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Long price,
        @NotBlank @Size(max = 30) String socket,
        @NotBlank @Size(max = 30) String chipset,
        @NotBlank @Size(max = 30) String formFactor,
        @NotBlank @Size(max = 20) String memorySupport,
        @NotNull @PositiveOrZero Integer memorySlots,
        @NotNull @PositiveOrZero Integer maxMemory,
        @NotNull @PositiveOrZero Integer maxMemoryClock,
        @NotBlank @Size(max = 20) String pcieVersion,
        @NotNull @PositiveOrZero Integer pcieX16Slots,
        @NotNull @PositiveOrZero Integer m2Slots,
        @NotNull @PositiveOrZero Integer sataPorts,
        @NotNull Boolean wifi,
        @NotNull Boolean bluetooth,
        String description,
        String imageUrl
) {
}
