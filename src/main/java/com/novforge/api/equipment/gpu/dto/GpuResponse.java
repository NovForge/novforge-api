package com.novforge.api.equipment.gpu.dto;

import com.novforge.api.equipment.gpu.Gpu;

import java.time.LocalDateTime;

public record GpuResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        Integer memorySize,
        String memoryType,
        Integer length,
        Integer powerConsumption,
        Integer recommendedPsu,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GpuResponse from(Gpu gpu) {
        return new GpuResponse(
                gpu.getId(),
                gpu.getManufacturer(),
                gpu.getName(),
                gpu.getPrice(),
                gpu.getMemorySize(),
                gpu.getMemoryType(),
                gpu.getLength(),
                gpu.getPowerConsumption(),
                gpu.getRecommendedPsu(),
                gpu.getDescription(),
                gpu.getImageUrl(),
                gpu.getCreatedAt(),
                gpu.getUpdatedAt()
        );
    }
}
