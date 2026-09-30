package com.novforge.api.equipment.memory;

import com.novforge.api.equipment.memory.dto.MemoryCreateRequest;
import com.novforge.api.equipment.memory.dto.MemoryResponse;
import com.novforge.api.equipment.memory.dto.MemoryUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemoryService {

    private final MemoryRepository repository;

    public MemoryService(MemoryRepository repository) {
        this.repository = repository;
    }

    public List<MemoryResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(MemoryResponse::from)
                .toList();
    }

    public MemoryResponse findById(Long id) {
        return MemoryResponse.from(getById(id));
    }

    @Transactional
    public MemoryResponse create(MemoryCreateRequest request) {
        return MemoryResponse.from(repository.saveAndFlush(new Memory(request)));
    }

    @Transactional
    public MemoryResponse update(Long id, MemoryUpdateRequest request) {
        Memory memory = getById(id);
        memory.patch(request);
        return MemoryResponse.from(repository.saveAndFlush(memory));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private Memory getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MemoryNotFoundException(id));
    }
}
