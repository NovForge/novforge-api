package com.novforge.api.equipment.cpucooler;

public class CpuCoolerNotFoundException extends RuntimeException {

    public CpuCoolerNotFoundException(Long id) {
        super("CPU 쿨러를 찾을 수 없습니다. id=" + id);
    }
}
