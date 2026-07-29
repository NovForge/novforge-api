package com.novforge.api.mybuild;

import java.util.Arrays;

public enum MyBuildSinglePartType {
    MOTHERBOARD("motherboard"),
    GPU("gpu"),
    CPU("cpu"),
    POWER_SUPPLY("power-supply"),
    CPU_COOLER("cpu-cooler"),
    CASE("case");

    private final String pathValue;

    MyBuildSinglePartType(String pathValue) {
        this.pathValue = pathValue;
    }

    public static MyBuildSinglePartType fromPathValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.pathValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidBuildPartException(
                        "제거할 수 없는 단일 부품 유형입니다: " + value));
    }
}
