package com.novforge.api.mybuild;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MyBuildStorageId implements Serializable {

    @Column(name = "build_id")
    private Long buildId;

    @Column(name = "storage_id")
    private Long storageId;

    protected MyBuildStorageId() {
    }

    public MyBuildStorageId(Long buildId, Long storageId) {
        this.buildId = buildId;
        this.storageId = storageId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MyBuildStorageId that)) return false;
        return Objects.equals(buildId, that.buildId) && Objects.equals(storageId, that.storageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildId, storageId);
    }
}
