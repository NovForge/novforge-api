package com.novforge.api.equipment.cpu.dto;

import com.novforge.api.equipment.cpu.Cpu;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CpuResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        String socket,
        Integer cores,
        Integer threads,
        BigDecimal baseClock,
        BigDecimal boostClock,
        String cache,
        Integer tdp,
        Boolean integratedGraphics,
        String memorySupport,
        String pcieVersion,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CpuResponse from(Cpu cpu) {
        return new CpuResponse(
                cpu.getId(),
                cpu.getManufacturer(),
                cpu.getName(),
                cpu.getPrice(),
                cpu.getSocket(),
                cpu.getCores(),
                cpu.getThreads(),
                cpu.getBaseClock(),
                cpu.getBoostClock(),
                cpu.getCache(),
                cpu.getTdp(),
                cpu.getIntegratedGraphics(),
                cpu.getMemorySupport(),
                cpu.getPcieVersion(),
                cpu.getDescription(),
                cpu.getImageUrl(),
                cpu.getCreatedAt(),
                cpu.getUpdatedAt()
        );
    }
}
