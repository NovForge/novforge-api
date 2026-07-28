package com.novforge.api.mybuild;

public class MyBuildNotFoundException extends RuntimeException {

    public MyBuildNotFoundException(Long id) {
        super("내 견적을 찾을 수 없습니다. buildId=" + id);
    }
}
