package com.novforge.api.equipment.cpu;

import com.novforge.api.equipment.cpu.dto.CpuCreateRequest;
import com.novforge.api.equipment.cpu.dto.CpuUpdateRequest;
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
@Table(name = "cpu")
public class Cpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cpu_id")
    private Long id;

    @Column(name = "cpu_manufacture", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "cpu_name", length = 100, nullable = false)
    private String name;

    @Column(name = "cpu_price", nullable = false)
    private Long price;

    @Column(name = "cpu_socket", length = 30, nullable = false)
    private String socket;

    @Column(name = "cpu_cores", nullable = false)
    private Integer cores;

    @Column(name = "cpu_threads", nullable = false)
    private Integer threads;

    @Column(name = "cpu_base_clock", precision = 3, scale = 2, nullable = false)
    private BigDecimal baseClock;

    @Column(name = "cpu_boost_clock", precision = 3, scale = 2, nullable = false)
    private BigDecimal boostClock;

    @Column(name = "cpu_cache", length = 30, nullable = false)
    private String cache;

    @Column(name = "cpu_tdp", nullable = false)
    private Integer tdp;

    @Column(name = "cpu_integrated_graphics", nullable = false)
    private Boolean integratedGraphics;

    @Column(name = "cpu_memory_support", length = 100, nullable = false)
    private String memorySupport;

    @Column(name = "cpu_pcie_version", length = 20, nullable = false)
    private String pcieVersion;

    @Column(name = "cpu_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cpu_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Cpu() {
    }

    public Cpu(CpuCreateRequest request) {
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

    public void replaceWith(CpuCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        socket = request.socket();
        cores = request.cores();
        threads = request.threads();
        baseClock = request.baseClock();
        boostClock = request.boostClock();
        cache = request.cache();
        tdp = request.tdp();
        integratedGraphics = request.integratedGraphics();
        memorySupport = request.memorySupport();
        pcieVersion = request.pcieVersion();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(CpuUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.socket() != null) socket = request.socket();
        if (request.cores() != null) cores = request.cores();
        if (request.threads() != null) threads = request.threads();
        if (request.baseClock() != null) baseClock = request.baseClock();
        if (request.boostClock() != null) boostClock = request.boostClock();
        if (request.cache() != null) cache = request.cache();
        if (request.tdp() != null) tdp = request.tdp();
        if (request.integratedGraphics() != null) integratedGraphics = request.integratedGraphics();
        if (request.memorySupport() != null) memorySupport = request.memorySupport();
        if (request.pcieVersion() != null) pcieVersion = request.pcieVersion();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getSocket() { return socket; }
    public Integer getCores() { return cores; }
    public Integer getThreads() { return threads; }
    public BigDecimal getBaseClock() { return baseClock; }
    public BigDecimal getBoostClock() { return boostClock; }
    public String getCache() { return cache; }
    public Integer getTdp() { return tdp; }
    public Boolean getIntegratedGraphics() { return integratedGraphics; }
    public String getMemorySupport() { return memorySupport; }
    public String getPcieVersion() { return pcieVersion; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
