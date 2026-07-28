package com.novforge.api.equipment.memory.dto;

import com.novforge.api.equipment.memory.Memory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MemoryResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        String type,
        Integer capacity,
        Integer clock,
        Integer moduleCount,
        Integer moduleCapacity,
        String formFactor,
        Integer casLatency,
        BigDecimal voltage,
        Boolean ecc,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MemoryResponse from(Memory memory) {
        return new MemoryResponse(
                memory.getId(),
                memory.getManufacturer(),
                memory.getName(),
                memory.getPrice(),
                memory.getType(),
                memory.getCapacity(),
                memory.getClock(),
                memory.getModuleCount(),
                memory.getModuleCapacity(),
                memory.getFormFactor(),
                memory.getCasLatency(),
                memory.getVoltage(),
                memory.getEcc(),
                memory.getDescription(),
                memory.getImageUrl(),
                memory.getCreatedAt(),
                memory.getUpdatedAt()
        );
    }
}
