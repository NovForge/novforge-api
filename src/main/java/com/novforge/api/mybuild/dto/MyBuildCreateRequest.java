package com.novforge.api.mybuild.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MyBuildCreateRequest(
        @NotBlank @Size(max = 100) String buildName,
        Long motherboardId,
        Long gpuId,
        Long cpuId,
        Long powerId,
        Long cpuCoolerId,
        Long caseId,
        @Valid List<PartQuantityRequest> memories,
        @Valid List<PartQuantityRequest> storages,
        @NotNull Boolean publicBuild
) {
    public record PartQuantityRequest(
            @NotNull @Positive Long id,
            @NotNull @Positive Integer quantity
    ) {
    }
}
