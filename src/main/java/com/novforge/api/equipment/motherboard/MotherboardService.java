package com.novforge.api.equipment.motherboard;

import com.novforge.api.equipment.motherboard.dto.MotherboardCreateRequest;
import com.novforge.api.equipment.motherboard.dto.MotherboardResponse;
import com.novforge.api.equipment.motherboard.dto.MotherboardUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MotherboardService {

    private final MotherboardRepository repository;

    public MotherboardService(MotherboardRepository repository) {
        this.repository = repository;
    }

    public List<MotherboardResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(MotherboardResponse::from)
                .toList();
    }

    public MotherboardResponse findById(Long id) {
        return MotherboardResponse.from(getById(id));
    }

    @Transactional
    public MotherboardResponse create(MotherboardCreateRequest request) {
        return MotherboardResponse.from(repository.saveAndFlush(new Motherboard(request)));
    }

    @Transactional
    public MotherboardResponse update(Long id, MotherboardUpdateRequest request) {
        Motherboard motherboard = getById(id);
        motherboard.patch(request);
        return MotherboardResponse.from(repository.saveAndFlush(motherboard));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private Motherboard getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MotherboardNotFoundException(id));
    }
}
