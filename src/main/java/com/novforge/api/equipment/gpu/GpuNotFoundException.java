package com.novforge.api.equipment.gpu;

public class GpuNotFoundException extends RuntimeException {

    public GpuNotFoundException(Long id) {
        super("GPU를 찾을 수 없습니다. id=" + id);
    }
}
