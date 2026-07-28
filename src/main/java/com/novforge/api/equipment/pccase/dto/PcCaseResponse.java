package com.novforge.api.equipment.pccase.dto;

import com.novforge.api.equipment.pccase.PcCase;

import java.time.LocalDateTime;

public record PcCaseResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        String type,
        String supportedFormFactor,
        Integer maxGpuLength,
        Integer maxCpuCoolerHeight,
        String supportedRadiatorSize,
        Integer fanCount,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PcCaseResponse from(PcCase pcCase) {
        return new PcCaseResponse(
                pcCase.getId(),
                pcCase.getManufacturer(),
                pcCase.getName(),
                pcCase.getPrice(),
                pcCase.getType(),
                pcCase.getSupportedFormFactor(),
                pcCase.getMaxGpuLength(),
                pcCase.getMaxCpuCoolerHeight(),
                pcCase.getSupportedRadiatorSize(),
                pcCase.getFanCount(),
                pcCase.getDescription(),
                pcCase.getImageUrl(),
                pcCase.getCreatedAt(),
                pcCase.getUpdatedAt()
        );
    }
}
