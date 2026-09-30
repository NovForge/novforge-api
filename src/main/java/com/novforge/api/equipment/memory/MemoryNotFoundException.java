package com.novforge.api.equipment.memory;

public class MemoryNotFoundException extends RuntimeException {

    public MemoryNotFoundException(Long id) {
        super("메모리를 찾을 수 없습니다. id=" + id);
    }
}
