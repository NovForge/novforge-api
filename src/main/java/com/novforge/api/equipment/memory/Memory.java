package com.novforge.api.equipment.memory;

import com.novforge.api.equipment.memory.dto.MemoryCreateRequest;
import com.novforge.api.equipment.memory.dto.MemoryUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "memory")
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memory_id")
    private Long id;

    @Column(name = "memory_manufacturer", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "memory_name", length = 255, nullable = false)
    private String name;

    @Column(name = "memory_price", nullable = false)
    private Long price;

    @Column(name = "memory_type", length = 20, nullable = false)
    private String type;

    @Column(name = "memory_capacity", nullable = false)
    private Integer capacity;

    @Column(name = "memory_clock", nullable = false)
    private Integer clock;

    @Column(name = "memory_module_count", nullable = false)
    private Integer moduleCount;

    @Column(name = "memory_module_capacity", nullable = false)
    private Integer moduleCapacity;

    @Column(name = "memory_form_factor", length = 20, nullable = false)
    private String formFactor;

    @Column(name = "memory_cas_latency", nullable = false)
    private Integer casLatency;

    @Column(name = "memory_voltage", precision = 3, scale = 2, nullable = false)
    private BigDecimal voltage;

    @Column(name = "memory_ecc", nullable = false)
    private Boolean ecc;

    @Column(name = "memory_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "memory_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    protected Memory() {
    }

    public Memory(MemoryCreateRequest request) {
        replaceWith(request);
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void replaceWith(MemoryCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        type = request.type();
        capacity = request.capacity();
        clock = request.clock();
        moduleCount = request.moduleCount();
        moduleCapacity = request.moduleCapacity();
        formFactor = request.formFactor();
        casLatency = request.casLatency();
        voltage = request.voltage();
        ecc = request.ecc();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(MemoryUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.type() != null) type = request.type();
        if (request.capacity() != null) capacity = request.capacity();
        if (request.clock() != null) clock = request.clock();
        if (request.moduleCount() != null) moduleCount = request.moduleCount();
        if (request.moduleCapacity() != null) moduleCapacity = request.moduleCapacity();
        if (request.formFactor() != null) formFactor = request.formFactor();
        if (request.casLatency() != null) casLatency = request.casLatency();
        if (request.voltage() != null) voltage = request.voltage();
        if (request.ecc() != null) ecc = request.ecc();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getType() { return type; }
    public Integer getCapacity() { return capacity; }
    public Integer getClock() { return clock; }
    public Integer getModuleCount() { return moduleCount; }
    public Integer getModuleCapacity() { return moduleCapacity; }
    public String getFormFactor() { return formFactor; }
    public Integer getCasLatency() { return casLatency; }
    public BigDecimal getVoltage() { return voltage; }
    public Boolean getEcc() { return ecc; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
