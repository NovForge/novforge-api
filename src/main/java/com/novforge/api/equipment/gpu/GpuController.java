package com.novforge.api.equipment.gpu;

import com.novforge.api.equipment.gpu.dto.GpuCreateRequest;
import com.novforge.api.equipment.gpu.dto.GpuResponse;
import com.novforge.api.equipment.gpu.dto.GpuUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/gpus")
public class GpuController {

    private final GpuService service;

    public GpuController(GpuService service) {
        this.service = service;
    }

    @GetMapping
    public List<GpuResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public GpuResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<GpuResponse> create(@Valid @RequestBody GpuCreateRequest request) {
        GpuResponse response = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}")
    public GpuResponse update(
            @PathVariable Long id,
            @Valid @RequestBody GpuUpdateRequest request
    ) {
        return service.update(id, request);
    }
}
