package com.novforge.api.equipment.powersupply;

import com.novforge.api.equipment.powersupply.dto.PowerSupplyCreateRequest;
import com.novforge.api.equipment.powersupply.dto.PowerSupplyResponse;
import com.novforge.api.equipment.powersupply.dto.PowerSupplyUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PowerSupplyService {

    private final PowerSupplyRepository repository;

    public PowerSupplyService(PowerSupplyRepository repository) {
        this.repository = repository;
    }

    public List<PowerSupplyResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(PowerSupplyResponse::from)
                .toList();
    }

    public PowerSupplyResponse findById(Long id) {
        return PowerSupplyResponse.from(getById(id));
    }

    @Transactional
    public PowerSupplyResponse create(PowerSupplyCreateRequest request) {
        return PowerSupplyResponse.from(repository.saveAndFlush(new PowerSupply(request)));
    }

    @Transactional
    public PowerSupplyResponse update(Long id, PowerSupplyUpdateRequest request) {
        PowerSupply powerSupply = getById(id);
        powerSupply.patch(request);
        return PowerSupplyResponse.from(repository.saveAndFlush(powerSupply));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private PowerSupply getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PowerSupplyNotFoundException(id));
    }
}
