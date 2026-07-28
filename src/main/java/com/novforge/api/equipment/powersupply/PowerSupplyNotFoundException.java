package com.novforge.api.equipment.powersupply;

public class PowerSupplyNotFoundException extends RuntimeException {

    public PowerSupplyNotFoundException(Long id) {
        super("파워 서플라이를 찾을 수 없습니다. id=" + id);
    }
}
