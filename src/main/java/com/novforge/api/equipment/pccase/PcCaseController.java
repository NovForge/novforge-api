package com.novforge.api.equipment.pccase;

import com.novforge.api.equipment.pccase.dto.PcCaseCreateRequest;
import com.novforge.api.equipment.pccase.dto.PcCaseResponse;
import com.novforge.api.equipment.pccase.dto.PcCaseUpdateRequest;
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
@RequestMapping("/api/cases")
public class PcCaseController {

    private final PcCaseService service;

    public PcCaseController(PcCaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<PcCaseResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PcCaseResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<PcCaseResponse> create(@Valid @RequestBody PcCaseCreateRequest request) {
        PcCaseResponse response = service.create(request);
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
    public PcCaseResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PcCaseUpdateRequest request
    ) {
        return service.update(id, request);
    }
}
