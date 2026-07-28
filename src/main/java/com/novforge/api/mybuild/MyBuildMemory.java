package com.novforge.api.mybuild;

import com.novforge.api.equipment.memory.Memory;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "my_build_ram")
public class MyBuildMemory {

    @EmbeddedId
    private MyBuildMemoryId id = new MyBuildMemoryId();

    @MapsId("buildId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_id", nullable = false)
    private MyBuild myBuild;

    @MapsId("memoryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "memory_id", nullable = false)
    private Memory memory;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    protected MyBuildMemory() {
    }

    public MyBuildMemory(MyBuild myBuild, Memory memory, Integer quantity) {
        this.myBuild = myBuild;
        this.memory = memory;
        this.quantity = quantity;
    }

    public void updateQuantity(Integer quantity) { this.quantity = quantity; }
    public Memory getMemory() { return memory; }
    public Integer getQuantity() { return quantity; }
}
