package com.novforge.api.equipment.gpu;

import com.novforge.api.equipment.gpu.dto.GpuCreateRequest;
import com.novforge.api.equipment.gpu.dto.GpuResponse;
import com.novforge.api.equipment.gpu.dto.GpuUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GpuService {

    private final GpuRepository repository;

    public GpuService(GpuRepository repository) {
        this.repository = repository;
    }

    public List<GpuResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(GpuResponse::from)
                .toList();
    }

    public GpuResponse findById(Long id) {
        return GpuResponse.from(getById(id));
    }

    @Transactional
    public GpuResponse create(GpuCreateRequest request) {
        return GpuResponse.from(repository.saveAndFlush(new Gpu(request)));
    }

    @Transactional
    public GpuResponse update(Long id, GpuUpdateRequest request) {
        Gpu gpu = getById(id);
        gpu.patch(request);
        return GpuResponse.from(repository.saveAndFlush(gpu));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private Gpu getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new GpuNotFoundException(id));
    }
}
