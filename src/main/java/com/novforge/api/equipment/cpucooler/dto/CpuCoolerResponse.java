package com.novforge.api.equipment.cpucooler.dto;

import com.novforge.api.equipment.cpucooler.CpuCooler;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CpuCoolerResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        String type,
        String socket,
        Integer fanSize,
        Integer radiatorSize,
        Integer height,
        BigDecimal airflow,
        BigDecimal noiseLevel,
        Boolean rgb,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CpuCoolerResponse from(CpuCooler cpuCooler) {
        return new CpuCoolerResponse(
                cpuCooler.getId(),
                cpuCooler.getManufacturer(),
                cpuCooler.getName(),
                cpuCooler.getPrice(),
                cpuCooler.getType(),
                cpuCooler.getSocket(),
                cpuCooler.getFanSize(),
                cpuCooler.getRadiatorSize(),
                cpuCooler.getHeight(),
                cpuCooler.getAirflow(),
                cpuCooler.getNoiseLevel(),
                cpuCooler.getRgb(),
                cpuCooler.getDescription(),
                cpuCooler.getImageUrl(),
                cpuCooler.getCreatedAt(),
                cpuCooler.getUpdatedAt()
        );
    }
}
