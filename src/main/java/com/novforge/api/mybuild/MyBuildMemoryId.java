package com.novforge.api.mybuild;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MyBuildMemoryId implements Serializable {

    @Column(name = "build_id")
    private Long buildId;

    @Column(name = "memory_id")
    private Long memoryId;

    protected MyBuildMemoryId() {
    }

    public MyBuildMemoryId(Long buildId, Long memoryId) {
        this.buildId = buildId;
        this.memoryId = memoryId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MyBuildMemoryId that)) return false;
        return Objects.equals(buildId, that.buildId) && Objects.equals(memoryId, that.memoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildId, memoryId);
    }
}
