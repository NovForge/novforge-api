package com.novforge.api.equipment.cpucooler;

import com.novforge.api.equipment.cpucooler.dto.CpuCoolerCreateRequest;
import com.novforge.api.equipment.cpucooler.dto.CpuCoolerResponse;
import com.novforge.api.equipment.cpucooler.dto.CpuCoolerUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CpuCoolerService {

    private final CpuCoolerRepository repository;

    public CpuCoolerService(CpuCoolerRepository repository) {
        this.repository = repository;
    }

    public List<CpuCoolerResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(CpuCoolerResponse::from)
                .toList();
    }

    public CpuCoolerResponse findById(Long id) {
        return CpuCoolerResponse.from(getById(id));
    }

    @Transactional
    public CpuCoolerResponse create(CpuCoolerCreateRequest request) {
        return CpuCoolerResponse.from(repository.saveAndFlush(new CpuCooler(request)));
    }

    @Transactional
    public CpuCoolerResponse update(Long id, CpuCoolerUpdateRequest request) {
        CpuCooler cpuCooler = getById(id);
        cpuCooler.patch(request);
        return CpuCoolerResponse.from(repository.saveAndFlush(cpuCooler));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private CpuCooler getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CpuCoolerNotFoundException(id));
    }
}
