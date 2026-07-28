package com.novforge.api.equipment.powersupply;

import com.novforge.api.equipment.powersupply.dto.PowerSupplyCreateRequest;
import com.novforge.api.equipment.powersupply.dto.PowerSupplyUpdateRequest;
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
@Table(name = "power")
public class PowerSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "power_id")
    private Long id;

    @Column(name = "power_manufacturer", length = 100, nullable = false)
    private String manufacturer;

    @Column(name = "power_name", length = 255, nullable = false)
    private String name;

    @Column(name = "power_price", nullable = false)
    private Long price;

    @Column(name = "power_wattage", nullable = false)
    private Integer wattage;

    @Column(name = "power_efficiency", length = 30, nullable = false)
    private String efficiency;

    @Column(name = "power_modular_type", length = 30, nullable = false)
    private String modularType;

    @Column(name = "power_form_factor", length = 20, nullable = false)
    private String formFactor;

    @Column(name = "power_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "power_image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected PowerSupply() {
    }

    public PowerSupply(PowerSupplyCreateRequest request) {
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

    public void replaceWith(PowerSupplyCreateRequest request) {
        manufacturer = request.manufacturer();
        name = request.name();
        price = request.price();
        wattage = request.wattage();
        efficiency = request.efficiency();
        modularType = request.modularType();
        formFactor = request.formFactor();
        description = request.description();
        imageUrl = request.imageUrl();
    }

    public void patch(PowerSupplyUpdateRequest request) {
        if (request.manufacturer() != null) manufacturer = request.manufacturer();
        if (request.name() != null) name = request.name();
        if (request.price() != null) price = request.price();
        if (request.wattage() != null) wattage = request.wattage();
        if (request.efficiency() != null) efficiency = request.efficiency();
        if (request.modularType() != null) modularType = request.modularType();
        if (request.formFactor() != null) formFactor = request.formFactor();
        if (request.description() != null) description = request.description();
        if (request.imageUrl() != null) imageUrl = request.imageUrl();
    }

    public Long getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public Integer getWattage() { return wattage; }
    public String getEfficiency() { return efficiency; }
    public String getModularType() { return modularType; }
    public String getFormFactor() { return formFactor; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
