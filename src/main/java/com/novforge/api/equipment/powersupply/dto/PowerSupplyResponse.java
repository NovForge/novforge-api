package com.novforge.api.equipment.powersupply.dto;

import com.novforge.api.equipment.powersupply.PowerSupply;

import java.time.LocalDateTime;

public record PowerSupplyResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        Integer wattage,
        String efficiency,
        String modularType,
        String formFactor,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PowerSupplyResponse from(PowerSupply powerSupply) {
        return new PowerSupplyResponse(
                powerSupply.getId(),
                powerSupply.getManufacturer(),
                powerSupply.getName(),
                powerSupply.getPrice(),
                powerSupply.getWattage(),
                powerSupply.getEfficiency(),
                powerSupply.getModularType(),
                powerSupply.getFormFactor(),
                powerSupply.getDescription(),
                powerSupply.getImageUrl(),
                powerSupply.getCreatedAt(),
                powerSupply.getUpdatedAt()
        );
    }
}
