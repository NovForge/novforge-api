package com.novforge.api.equipment.motherboard;

import com.novforge.api.equipment.motherboard.dto.MotherboardCreateRequest;
import com.novforge.api.equipment.motherboard.dto.MotherboardResponse;
import com.novforge.api.equipment.motherboard.dto.MotherboardUpdateRequest;
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
@RequestMapping({"/api/mainboards", "/api/motherboards"})
public class MotherboardController {

    private final MotherboardService service;

    public MotherboardController(MotherboardService service) {
        this.service = service;
    }

    @GetMapping
    public List<MotherboardResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MotherboardResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<MotherboardResponse> create(@Valid @RequestBody MotherboardCreateRequest request) {
        MotherboardResponse response = service.create(request);
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
    public MotherboardResponse update(
            @PathVariable Long id,
            @Valid @RequestBody MotherboardUpdateRequest request
    ) {
        return service.update(id, request);
    }
}
