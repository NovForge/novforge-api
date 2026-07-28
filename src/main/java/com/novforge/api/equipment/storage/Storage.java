package com.novforge.api.equipment.storage;

import com.novforge.api.equipment.storage.dto.StorageCreateRequest;
import com.novforge.api.equipment.storage.dto.StorageUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Long id;

    @Column(name = "storage_manufacturer", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "storage_name", length = 255, nullable = false)
    private String name;

    @Column(name = "storage_price", nullable = false)
    private Long price;

    @Column(name = "storage_type", length = 20, nullable = false)
    private String type;

    @Column(name = "storage_interface", length = 30, nullable = false)
    private String interfaceType;

    @Column(name = "storage_capacity", nullable = false)
    private Integer capacity;

    @Column(name = "storage_read_speed", nullable = false)
    private Integer readSpeed;

    @Column(name = "storage_form_factor", length = 20, nullable = false)
    private String formFactor;

    @Column(name = "storage_cache_size", nullable = false)
    private Integer cacheSize;

    @Column(name = "storage_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "storage_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Storage() {
    }

    public Storage(StorageCreateRequest request) {
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

    public void replaceWith(StorageCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        type = request.type();
        interfaceType = request.interfaceType();
        capacity = request.capacity();
        readSpeed = request.readSpeed();
        formFactor = request.formFactor();
        cacheSize = request.cacheSize();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(StorageUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.type() != null) type = request.type();
        if (request.interfaceType() != null) interfaceType = request.interfaceType();
        if (request.capacity() != null) capacity = request.capacity();
        if (request.readSpeed() != null) readSpeed = request.readSpeed();
        if (request.formFactor() != null) formFactor = request.formFactor();
        if (request.cacheSize() != null) cacheSize = request.cacheSize();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getType() { return type; }
    public String getInterfaceType() { return interfaceType; }
    public Integer getCapacity() { return capacity; }
    public Integer getReadSpeed() { return readSpeed; }
    public String getFormFactor() { return formFactor; }
    public Integer getCacheSize() { return cacheSize; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
