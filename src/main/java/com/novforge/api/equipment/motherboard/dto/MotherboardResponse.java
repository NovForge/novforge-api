package com.novforge.api.equipment.motherboard.dto;

import com.novforge.api.equipment.motherboard.Motherboard;

import java.time.LocalDateTime;

public record MotherboardResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        String socket,
        String chipset,
        String formFactor,
        String memorySupport,
        Integer memorySlots,
        Integer maxMemory,
        Integer maxMemoryClock,
        String pcieVersion,
        Integer pcieX16Slots,
        Integer m2Slots,
        Integer sataPorts,
        Boolean wifi,
        Boolean bluetooth,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MotherboardResponse from(Motherboard motherboard) {
        return new MotherboardResponse(
                motherboard.getId(),
                motherboard.getManufacturer(),
                motherboard.getName(),
                motherboard.getPrice(),
                motherboard.getSocket(),
                motherboard.getChipset(),
                motherboard.getFormFactor(),
                motherboard.getMemorySupport(),
                motherboard.getMemorySlots(),
                motherboard.getMaxMemory(),
                motherboard.getMaxMemoryClock(),
                motherboard.getPcieVersion(),
                motherboard.getPcieX16Slots(),
                motherboard.getM2Slots(),
                motherboard.getSataPorts(),
                motherboard.getWifi(),
                motherboard.getBluetooth(),
                motherboard.getDescription(),
                motherboard.getImageUrl(),
                motherboard.getCreatedAt(),
                motherboard.getUpdatedAt()
        );
    }
}
