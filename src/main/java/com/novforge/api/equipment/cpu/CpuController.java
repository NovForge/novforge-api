package com.novforge.api.equipment.cpu;

import com.novforge.api.equipment.cpu.dto.CpuCreateRequest;
import com.novforge.api.equipment.cpu.dto.CpuResponse;
import com.novforge.api.equipment.cpu.dto.CpuUpdateRequest;
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
@RequestMapping("/api/cpus")
public class CpuController {

    private final CpuService service;

    public CpuController(CpuService service) {
        this.service = service;
    }

    @GetMapping
    public List<CpuResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CpuResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<CpuResponse> create(@Valid @RequestBody CpuCreateRequest request) {
        CpuResponse response = service.create(request);
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
    public CpuResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CpuUpdateRequest request
    ) {
        return service.update(id, request);
    }
}
