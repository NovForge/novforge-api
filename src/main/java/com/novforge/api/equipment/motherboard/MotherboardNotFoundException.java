package com.novforge.api.equipment.motherboard;

public class MotherboardNotFoundException extends RuntimeException {

    public MotherboardNotFoundException(Long id) {
        super("메인보드를 찾을 수 없습니다. id=" + id);
    }
}
