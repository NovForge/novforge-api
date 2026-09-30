package com.novforge.api.equipment.storage;

import com.novforge.api.equipment.storage.dto.StorageCreateRequest;
import com.novforge.api.equipment.storage.dto.StorageResponse;
import com.novforge.api.equipment.storage.dto.StorageUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StorageService {

    private final StorageRepository repository;

    public StorageService(StorageRepository repository) {
        this.repository = repository;
    }

    public List<StorageResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(StorageResponse::from)
                .toList();
    }

    public StorageResponse findById(Long id) {
        return StorageResponse.from(getById(id));
    }

    @Transactional
    public StorageResponse create(StorageCreateRequest request) {
        return StorageResponse.from(repository.saveAndFlush(new Storage(request)));
    }

    @Transactional
    public StorageResponse update(Long id, StorageUpdateRequest request) {
        Storage storage = getById(id);
        storage.patch(request);
        return StorageResponse.from(repository.saveAndFlush(storage));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private Storage getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new StorageNotFoundException(id));
    }
}
