package com.novforge.api.equipment.cpu;

public class CpuNotFoundException extends RuntimeException {

    public CpuNotFoundException(Long id) {
        super("CPU를 찾을 수 없습니다. id=" + id);
    }
}
