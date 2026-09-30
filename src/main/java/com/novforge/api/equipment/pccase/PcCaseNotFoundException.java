package com.novforge.api.equipment.pccase;

public class PcCaseNotFoundException extends RuntimeException {

    public PcCaseNotFoundException(Long id) {
        super("케이스를 찾을 수 없습니다. id=" + id);
    }
}
