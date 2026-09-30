package com.novforge.api.equipment.cpucooler;

import com.novforge.api.equipment.cpucooler.dto.CpuCoolerCreateRequest;
import com.novforge.api.equipment.cpucooler.dto.CpuCoolerResponse;
import com.novforge.api.equipment.cpucooler.dto.CpuCoolerUpdateRequest;
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
@RequestMapping("/api/cpu-coolers")
public class CpuCoolerController {

    private final CpuCoolerService service;

    public CpuCoolerController(CpuCoolerService service) {
        this.service = service;
    }

    @GetMapping
    public List<CpuCoolerResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CpuCoolerResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<CpuCoolerResponse> create(@Valid @RequestBody CpuCoolerCreateRequest request) {
        CpuCoolerResponse response = service.create(request);
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
    public CpuCoolerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CpuCoolerUpdateRequest request
    ) {
        return service.update(id, request);
    }
}
