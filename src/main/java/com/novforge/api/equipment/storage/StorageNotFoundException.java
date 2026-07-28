package com.novforge.api.equipment.storage;

public class StorageNotFoundException extends RuntimeException {

    public StorageNotFoundException(Long id) {
        super("보조기억장치를 찾을 수 없습니다. id=" + id);
    }
}
