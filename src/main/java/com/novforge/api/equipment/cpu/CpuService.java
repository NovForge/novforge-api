package com.novforge.api.equipment.cpu;

import com.novforge.api.equipment.cpu.dto.CpuCreateRequest;
import com.novforge.api.equipment.cpu.dto.CpuResponse;
import com.novforge.api.equipment.cpu.dto.CpuUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CpuService {

    private final CpuRepository repository;

    public CpuService(CpuRepository repository) {
        this.repository = repository;
    }

    public List<CpuResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(CpuResponse::from)
                .toList();
    }

    public CpuResponse findById(Long id) {
        return CpuResponse.from(getById(id));
    }

    @Transactional
    public CpuResponse create(CpuCreateRequest request) {
        return CpuResponse.from(repository.saveAndFlush(new Cpu(request)));
    }

    @Transactional
    public CpuResponse update(Long id, CpuUpdateRequest request) {
        Cpu cpu = getById(id);
        cpu.patch(request);
        return CpuResponse.from(repository.saveAndFlush(cpu));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private Cpu getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CpuNotFoundException(id));
    }
}
