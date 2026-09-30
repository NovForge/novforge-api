package com.novforge.api.mybuild.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MyBuildUpdateRequest(
        @Size(min = 1, max = 100) String buildName,
        Long motherboardId,
        Long gpuId,
        Long cpuId,
        Long powerId,
        Long cpuCoolerId,
        Long caseId,
        @Valid List<MyBuildCreateRequest.PartQuantityRequest> memories,
        @Valid List<MyBuildCreateRequest.PartQuantityRequest> storages,
        Boolean publicBuild
) {
}
