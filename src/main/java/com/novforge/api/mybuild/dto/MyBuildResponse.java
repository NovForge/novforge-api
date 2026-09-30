package com.novforge.api.mybuild.dto;

import com.novforge.api.mybuild.MyBuild;

import java.time.LocalDateTime;
import java.util.List;

public record MyBuildResponse(
        Long buildId,
        Long userId,
        String buildName,
        Long totalPrice,
        Boolean publicBuild,
        PartResponse motherboard,
        PartResponse gpu,
        PartResponse cpu,
        PartResponse powerSupply,
        PartResponse cpuCooler,
        PartResponse pcCase,
        List<PartQuantityResponse> memories,
        List<PartQuantityResponse> storages,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MyBuildResponse from(MyBuild build) {
        return new MyBuildResponse(
                build.getId(),
                build.getUser().getId(),
                build.getName(),
                build.getTotalPrice(),
                build.getPublicBuild(),
                build.getMotherboard() == null ? null : new PartResponse(
                        build.getMotherboard().getId(), build.getMotherboard().getName(), build.getMotherboard().getPrice()),
                build.getGpu() == null ? null : new PartResponse(
                        build.getGpu().getId(), build.getGpu().getName(), build.getGpu().getPrice()),
                build.getCpu() == null ? null : new PartResponse(
                        build.getCpu().getId(), build.getCpu().getName(), build.getCpu().getPrice()),
                build.getPowerSupply() == null ? null : new PartResponse(
                        build.getPowerSupply().getId(), build.getPowerSupply().getName(), build.getPowerSupply().getPrice()),
                build.getCpuCooler() == null ? null : new PartResponse(
                        build.getCpuCooler().getId(), build.getCpuCooler().getName(), build.getCpuCooler().getPrice()),
                build.getPcCase() == null ? null : new PartResponse(
                        build.getPcCase().getId(), build.getPcCase().getName(), build.getPcCase().getPrice()),
                build.getMemories().stream()
                        .map(item -> new PartQuantityResponse(
                                item.getMemory().getId(),
                                item.getMemory().getName(),
                                item.getMemory().getPrice(),
                                item.getQuantity()))
                        .toList(),
                build.getStorages().stream()
                        .map(item -> new PartQuantityResponse(
                                item.getStorage().getId(),
                                item.getStorage().getName(),
                                item.getStorage().getPrice(),
                                item.getQuantity()))
                        .toList(),
                build.getCreatedAt(),
                build.getUpdatedAt()
        );
    }

    public record PartResponse(Long id, String name, Long price) {
    }

    public record PartQuantityResponse(Long id, String name, Long price, Integer quantity) {
    }
}
