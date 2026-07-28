package com.novforge.api.mybuild;

import com.novforge.api.equipment.storage.Storage;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "my_build_storage")
public class MyBuildStorage {

    @EmbeddedId
    private MyBuildStorageId id = new MyBuildStorageId();

    @MapsId("buildId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_id", nullable = false)
    private MyBuild myBuild;

    @MapsId("storageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "storage_id", nullable = false)
    private Storage storage;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    protected MyBuildStorage() {
    }

    public MyBuildStorage(MyBuild myBuild, Storage storage, Integer quantity) {
        this.myBuild = myBuild;
        this.storage = storage;
        this.quantity = quantity;
    }

    public void updateQuantity(Integer quantity) { this.quantity = quantity; }
    public Storage getStorage() { return storage; }
    public Integer getQuantity() { return quantity; }
}
