package com.novforge.api.equipment.gpu;

import com.novforge.api.equipment.gpu.dto.GpuCreateRequest;
import com.novforge.api.equipment.gpu.dto.GpuUpdateRequest;
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
@Table(name = "gpu")
public class Gpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gpu_id")
    private Long id;

    @Column(name = "gpu_manufacturer", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "gpu_name", length = 255, nullable = false)
    private String name;

    @Column(name = "gpu_price", nullable = false)
    private Long price;

    @Column(name = "gpu_memory_size", nullable = false)
    private Integer memorySize;

    @Column(name = "gpu_memory_type", length = 20, nullable = false)
    private String memoryType;

    @Column(name = "gpu_length", nullable = false)
    private Integer length;

    @Column(name = "gpu_power_consumption", nullable = false)
    private Integer powerConsumption;

    @Column(name = "gpu_recommended_psu", nullable = false)
    private Integer recommendedPsu;

    @Column(name = "gpu_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "gpu_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Gpu() {
    }

    public Gpu(GpuCreateRequest request) {
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

    public void replaceWith(GpuCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        memorySize = request.memorySize();
        memoryType = request.memoryType();
        length = request.length();
        powerConsumption = request.powerConsumption();
        recommendedPsu = request.recommendedPsu();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(GpuUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.memorySize() != null) memorySize = request.memorySize();
        if (request.memoryType() != null) memoryType = request.memoryType();
        if (request.length() != null) length = request.length();
        if (request.powerConsumption() != null) powerConsumption = request.powerConsumption();
        if (request.recommendedPsu() != null) recommendedPsu = request.recommendedPsu();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public Integer getMemorySize() { return memorySize; }
    public String getMemoryType() { return memoryType; }
    public Integer getLength() { return length; }
    public Integer getPowerConsumption() { return powerConsumption; }
    public Integer getRecommendedPsu() { return recommendedPsu; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
