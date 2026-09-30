package com.novforge.api.equipment.pccase;

import com.novforge.api.equipment.pccase.dto.PcCaseCreateRequest;
import com.novforge.api.equipment.pccase.dto.PcCaseUpdateRequest;
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
@Table(name = "\"case\"")
public class PcCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long id;

    @Column(name = "case_manufacturer", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "case_name", length = 255, nullable = false)
    private String name;

    @Column(name = "case_price", nullable = false)
    private Long price;

    @Column(name = "case_type", length = 30, nullable = false)
    private String type;

    @Column(name = "case_supported_form_factor", length = 100, nullable = false)
    private String supportedFormFactor;

    @Column(name = "case_max_gpu_length", nullable = false)
    private Integer maxGpuLength;

    @Column(name = "case_max_cpu_cooler_height", nullable = false)
    private Integer maxCpuCoolerHeight;

    @Column(name = "case_supported_radiator_size", length = 100, nullable = false)
    private String supportedRadiatorSize;

    @Column(name = "case_fan_count", nullable = false)
    private Integer fanCount;

    @Column(name = "case_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "case_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected PcCase() {
    }

    public PcCase(PcCaseCreateRequest request) {
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

    public void replaceWith(PcCaseCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        type = request.type();
        supportedFormFactor = request.supportedFormFactor();
        maxGpuLength = request.maxGpuLength();
        maxCpuCoolerHeight = request.maxCpuCoolerHeight();
        supportedRadiatorSize = request.supportedRadiatorSize();
        fanCount = request.fanCount();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(PcCaseUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.type() != null) type = request.type();
        if (request.supportedFormFactor() != null) supportedFormFactor = request.supportedFormFactor();
        if (request.maxGpuLength() != null) maxGpuLength = request.maxGpuLength();
        if (request.maxCpuCoolerHeight() != null) maxCpuCoolerHeight = request.maxCpuCoolerHeight();
        if (request.supportedRadiatorSize() != null) supportedRadiatorSize = request.supportedRadiatorSize();
        if (request.fanCount() != null) fanCount = request.fanCount();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getType() { return type; }
    public String getSupportedFormFactor() { return supportedFormFactor; }
    public Integer getMaxGpuLength() { return maxGpuLength; }
    public Integer getMaxCpuCoolerHeight() { return maxCpuCoolerHeight; }
    public String getSupportedRadiatorSize() { return supportedRadiatorSize; }
    public Integer getFanCount() { return fanCount; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
