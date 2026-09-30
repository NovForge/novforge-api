package com.novforge.api.equipment.storage.dto;

import com.novforge.api.equipment.storage.Storage;

import java.time.LocalDateTime;

public record StorageResponse(
        Long id,
        String manufacturer,
        String name,
        Long price,
        String type,
        String interfaceType,
        Integer capacity,
        Integer readSpeed,
        String formFactor,
        Integer cacheSize,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StorageResponse from(Storage storage) {
        return new StorageResponse(
                storage.getId(),
                storage.getManufacturer(),
                storage.getName(),
                storage.getPrice(),
                storage.getType(),
                storage.getInterfaceType(),
                storage.getCapacity(),
                storage.getReadSpeed(),
                storage.getFormFactor(),
                storage.getCacheSize(),
                storage.getDescription(),
                storage.getImageUrl(),
                storage.getCreatedAt(),
                storage.getUpdatedAt()
        );
    }
}
