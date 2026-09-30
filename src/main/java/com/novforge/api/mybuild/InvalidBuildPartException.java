package com.novforge.api.mybuild;

public class InvalidBuildPartException extends RuntimeException {

    public InvalidBuildPartException(String part, Long id) {
        super("선택한 " + part + " 부품을 찾을 수 없습니다. id=" + id);
    }

    public InvalidBuildPartException(String message) {
        super(message);
    }
}
