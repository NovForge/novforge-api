package com.novforge.api.equipment.pccase;

import com.novforge.api.equipment.pccase.dto.PcCaseCreateRequest;
import com.novforge.api.equipment.pccase.dto.PcCaseResponse;
import com.novforge.api.equipment.pccase.dto.PcCaseUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PcCaseService {

    private final PcCaseRepository repository;

    public PcCaseService(PcCaseRepository repository) {
        this.repository = repository;
    }

    public List<PcCaseResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(PcCaseResponse::from)
                .toList();
    }

    public PcCaseResponse findById(Long id) {
        return PcCaseResponse.from(getById(id));
    }

    @Transactional
    public PcCaseResponse create(PcCaseCreateRequest request) {
        return PcCaseResponse.from(repository.saveAndFlush(new PcCase(request)));
    }

    @Transactional
    public PcCaseResponse update(Long id, PcCaseUpdateRequest request) {
        PcCase pcCase = getById(id);
        pcCase.patch(request);
        return PcCaseResponse.from(repository.saveAndFlush(pcCase));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private PcCase getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PcCaseNotFoundException(id));
    }
}
