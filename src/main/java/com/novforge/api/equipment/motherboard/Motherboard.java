package com.novforge.api.equipment.motherboard;

import com.novforge.api.equipment.motherboard.dto.MotherboardCreateRequest;
import com.novforge.api.equipment.motherboard.dto.MotherboardUpdateRequest;
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
@Table(name = "motherboard")
public class Motherboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "motherboard_id")
    private Long id;

    @Column(name = "motherboard_manufacturer", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "motherboard_name", length = 255, nullable = false)
    private String name;

    @Column(name = "motherboard_price", nullable = false)
    private Long price;

    @Column(name = "motherboard_socket", length = 30, nullable = false)
    private String socket;

    @Column(name = "motherboard_chipset", length = 30, nullable = false)
    private String chipset;

    @Column(name = "motherboard_form_factor", length = 30, nullable = false)
    private String formFactor;

    @Column(name = "motherboard_memory_support", length = 20, nullable = false)
    private String memorySupport;

    @Column(name = "motherboard_memory_slots", nullable = false)
    private Integer memorySlots;

    @Column(name = "motherboard_max_memory", nullable = false)
    private Integer maxMemory;

    @Column(name = "motherboard_max_memory_clock", nullable = false)
    private Integer maxMemoryClock;

    @Column(name = "motherboard_pcie_version", length = 20, nullable = false)
    private String pcieVersion;

    @Column(name = "motherboard_pcie_x16_slots", nullable = false)
    private Integer pcieX16Slots;

    @Column(name = "motherboard_m2_slots", nullable = false)
    private Integer m2Slots;

    @Column(name = "motherboard_sata_ports", nullable = false)
    private Integer sataPorts;

    @Column(name = "motherboard_wifi", nullable = false)
    private Boolean wifi;

    @Column(name = "motherboard_bluetooth", nullable = false)
    private Boolean bluetooth;

    @Column(name = "motherboard_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "motherboard_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Motherboard() {
    }

    public Motherboard(MotherboardCreateRequest request) {
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

    public void replaceWith(MotherboardCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        socket = request.socket();
        chipset = request.chipset();
        formFactor = request.formFactor();
        memorySupport = request.memorySupport();
        memorySlots = request.memorySlots();
        maxMemory = request.maxMemory();
        maxMemoryClock = request.maxMemoryClock();
        pcieVersion = request.pcieVersion();
        pcieX16Slots = request.pcieX16Slots();
        m2Slots = request.m2Slots();
        sataPorts = request.sataPorts();
        wifi = request.wifi();
        bluetooth = request.bluetooth();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(MotherboardUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.socket() != null) socket = request.socket();
        if (request.chipset() != null) chipset = request.chipset();
        if (request.formFactor() != null) formFactor = request.formFactor();
        if (request.memorySupport() != null) memorySupport = request.memorySupport();
        if (request.memorySlots() != null) memorySlots = request.memorySlots();
        if (request.maxMemory() != null) maxMemory = request.maxMemory();
        if (request.maxMemoryClock() != null) maxMemoryClock = request.maxMemoryClock();
        if (request.pcieVersion() != null) pcieVersion = request.pcieVersion();
        if (request.pcieX16Slots() != null) pcieX16Slots = request.pcieX16Slots();
        if (request.m2Slots() != null) m2Slots = request.m2Slots();
        if (request.sataPorts() != null) sataPorts = request.sataPorts();
        if (request.wifi() != null) wifi = request.wifi();
        if (request.bluetooth() != null) bluetooth = request.bluetooth();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getSocket() { return socket; }
    public String getChipset() { return chipset; }
    public String getFormFactor() { return formFactor; }
    public String getMemorySupport() { return memorySupport; }
    public Integer getMemorySlots() { return memorySlots; }
    public Integer getMaxMemory() { return maxMemory; }
    public Integer getMaxMemoryClock() { return maxMemoryClock; }
    public String getPcieVersion() { return pcieVersion; }
    public Integer getPcieX16Slots() { return pcieX16Slots; }
    public Integer getM2Slots() { return m2Slots; }
    public Integer getSataPorts() { return sataPorts; }
    public Boolean getWifi() { return wifi; }
    public Boolean getBluetooth() { return bluetooth; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
