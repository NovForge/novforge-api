package com.novforge.api.equipment.motherboard.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MotherboardUpdateRequest(
        @Size(min = 1, max = 100) String manufacturer,
        @Size(min = 1, max = 255) String name,
        @PositiveOrZero Long price,
        @Size(min = 1, max = 30) String socket,
        @Size(min = 1, max = 30) String chipset,
        @Size(min = 1, max = 30) String formFactor,
        @Size(min = 1, max = 20) String memorySupport,
        @PositiveOrZero Integer memorySlots,
        @PositiveOrZero Integer maxMemory,
        @PositiveOrZero Integer maxMemoryClock,
        @Size(min = 1, max = 20) String pcieVersion,
        @PositiveOrZero Integer pcieX16Slots,
        @PositiveOrZero Integer m2Slots,
        @PositiveOrZero Integer sataPorts,
        Boolean wifi,
        Boolean bluetooth,
        String description,
        String imageUrl
) {
}
