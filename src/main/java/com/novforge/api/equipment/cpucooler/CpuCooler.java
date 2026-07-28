package com.novforge.api.equipment.cpucooler;

import com.novforge.api.equipment.cpucooler.dto.CpuCoolerCreateRequest;
import com.novforge.api.equipment.cpucooler.dto.CpuCoolerUpdateRequest;
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
@Table(name = "cpu_cooler")
public class CpuCooler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cpu_cooler_id")
    private Long id;

    @Column(name = "cpu_cooler_manufacturer", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "cpu_cooler_name", length = 255, nullable = false)
    private String name;

    @Column(name = "cpu_cooler_price", nullable = false)
    private Long price;

    @Column(name = "cpu_cooler_type", length = 20, nullable = false)
    private String type;

    @Column(name = "cpu_cooler_socket", length = 100, nullable = false)
    private String socket;

    @Column(name = "cpu_cooler_fan_size", nullable = false)
    private Integer fanSize;

    @Column(name = "cpu_cooler_radiator_size", nullable = false)
    private Integer radiatorSize;

    @Column(name = "cpu_cooler_height", nullable = false)
    private Integer height;

    @Column(name = "cpu_cooler_airflow", precision = 5, scale = 2, nullable = false)
    private BigDecimal airflow;

    @Column(name = "cpu_cooler_noise_level", precision = 4, scale = 1, nullable = false)
    private BigDecimal noiseLevel;

    @Column(name = "cpu_cooler_rgb", nullable = false)
    private Boolean rgb;

    @Column(name = "cpu_cooler_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cpu_cooler_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected CpuCooler() {
    }

    public CpuCooler(CpuCoolerCreateRequest request) {
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

    public void replaceWith(CpuCoolerCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        type = request.type();
        socket = request.socket();
        fanSize = request.fanSize();
        radiatorSize = request.radiatorSize();
        height = request.height();
        airflow = request.airflow();
        noiseLevel = request.noiseLevel();
        rgb = request.rgb();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(CpuCoolerUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.type() != null) type = request.type();
        if (request.socket() != null) socket = request.socket();
        if (request.fanSize() != null) fanSize = request.fanSize();
        if (request.radiatorSize() != null) radiatorSize = request.radiatorSize();
        if (request.height() != null) height = request.height();
        if (request.airflow() != null) airflow = request.airflow();
        if (request.noiseLevel() != null) noiseLevel = request.noiseLevel();
        if (request.rgb() != null) rgb = request.rgb();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getType() { return type; }
    public String getSocket() { return socket; }
    public Integer getFanSize() { return fanSize; }
    public Integer getRadiatorSize() { return radiatorSize; }
    public Integer getHeight() { return height; }
    public BigDecimal getAirflow() { return airflow; }
    public BigDecimal getNoiseLevel() { return noiseLevel; }
    public Boolean getRgb() { return rgb; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
