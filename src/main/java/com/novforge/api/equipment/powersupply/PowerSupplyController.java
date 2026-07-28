package com.novforge.api.equipment.powersupply;

import com.novforge.api.equipment.powersupply.dto.PowerSupplyCreateRequest;
import com.novforge.api.equipment.powersupply.dto.PowerSupplyResponse;
import com.novforge.api.equipment.powersupply.dto.PowerSupplyUpdateRequest;
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
@RequestMapping("/api/power-supplies")
public class PowerSupplyController {

    private final PowerSupplyService service;

    public PowerSupplyController(PowerSupplyService service) {
        this.service = service;
    }

    @GetMapping
    public List<PowerSupplyResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PowerSupplyResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<PowerSupplyResponse> create(@Valid @RequestBody PowerSupplyCreateRequest request) {
        PowerSupplyResponse response = service.create(request);
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
    public PowerSupplyResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PowerSupplyUpdateRequest request
    ) {
        return service.update(id, request);
    }
}
