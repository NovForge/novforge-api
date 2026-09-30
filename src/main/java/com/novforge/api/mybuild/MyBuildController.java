package com.novforge.api.mybuild;

import com.novforge.api.mybuild.dto.MyBuildCreateRequest;
import com.novforge.api.mybuild.dto.MyBuildResponse;
import com.novforge.api.mybuild.dto.MyBuildUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/my-builds")
public class MyBuildController {

    private final MyBuildService service;

    public MyBuildController(MyBuildService service) {
        this.service = service;
    }

    @GetMapping
    public List<MyBuildResponse> findPublicBuilds() {
        return service.findPublicBuilds();
    }

    @GetMapping("/{buildId}")
    public MyBuildResponse findPublicBuild(@PathVariable Long buildId) {
        return service.findPublicBuild(buildId);
    }

    @GetMapping("/me")
    public List<MyBuildResponse> findMyBuilds(@AuthenticationPrincipal Jwt jwt) {
        return service.findMyBuilds(jwt);
    }

    @GetMapping("/me/{buildId}")
    public MyBuildResponse findMyBuild(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long buildId
    ) {
        return service.findMyBuild(jwt, buildId);
    }

    @PostMapping("/me")
    public ResponseEntity<MyBuildResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MyBuildCreateRequest request
    ) {
        MyBuildResponse response = service.create(jwt, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{buildId}")
                .buildAndExpand(response.buildId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/me/{buildId}")
    public MyBuildResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long buildId,
            @Valid @RequestBody MyBuildUpdateRequest request
    ) {
        return service.update(jwt, buildId, request);
    }

    @DeleteMapping("/me/{buildId}/parts/{partType}")
    public MyBuildResponse removeSinglePart(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long buildId,
            @PathVariable String partType
    ) {
        return service.removeSinglePart(
                jwt,
                buildId,
                MyBuildSinglePartType.fromPathValue(partType)
        );
    }

    @DeleteMapping("/me/{buildId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long buildId
    ) {
        service.delete(jwt, buildId);
    }
}
