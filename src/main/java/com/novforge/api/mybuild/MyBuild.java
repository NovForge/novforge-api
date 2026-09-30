package com.novforge.api.mybuild;

import com.novforge.api.equipment.cpu.Cpu;
import com.novforge.api.equipment.cpucooler.CpuCooler;
import com.novforge.api.equipment.gpu.Gpu;
import com.novforge.api.equipment.motherboard.Motherboard;
import com.novforge.api.equipment.pccase.PcCase;
import com.novforge.api.equipment.powersupply.PowerSupply;
import com.novforge.api.users.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "my_build")
public class MyBuild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "build_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motherboard_id")
    private Motherboard motherboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpu_id")
    private Gpu gpu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpu_id")
    private Cpu cpu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "power_id")
    private PowerSupply powerSupply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpu_cooler_id")
    private CpuCooler cpuCooler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private PcCase pcCase;

    @OneToMany(mappedBy = "myBuild", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<MyBuildMemory> memories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "myBuild", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<MyBuildStorage> storages = new LinkedHashSet<>();

    @Column(name = "build_name", length = 100, nullable = false)
    private String name;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "is_public", nullable = false)
    private Boolean publicBuild;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected MyBuild() {
    }

    public MyBuild(User user, String name, boolean publicBuild) {
        this.user = user;
        this.name = name;
        this.publicBuild = publicBuild;
        this.totalPrice = 0L;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateBasicInfo(String name, Boolean publicBuild) {
        if (name != null) this.name = name;
        if (publicBuild != null) this.publicBuild = publicBuild;
    }

    public void updateParts(
            Motherboard motherboard,
            Gpu gpu,
            Cpu cpu,
            PowerSupply powerSupply,
            CpuCooler cpuCooler,
            PcCase pcCase
    ) {
        this.motherboard = motherboard;
        this.gpu = gpu;
        this.cpu = cpu;
        this.powerSupply = powerSupply;
        this.cpuCooler = cpuCooler;
        this.pcCase = pcCase;
    }

    public void removeSinglePart(MyBuildSinglePartType partType) {
        switch (partType) {
            case MOTHERBOARD -> motherboard = null;
            case GPU -> gpu = null;
            case CPU -> cpu = null;
            case POWER_SUPPLY -> powerSupply = null;
            case CPU_COOLER -> cpuCooler = null;
            case CASE -> pcCase = null;
        }
    }

    public void replaceMemories(List<MyBuildMemory> newMemories) {
        memories.removeIf(current -> newMemories.stream()
                .noneMatch(next -> next.getMemory().getId().equals(current.getMemory().getId())));
        for (MyBuildMemory next : newMemories) {
            memories.stream()
                    .filter(current -> current.getMemory().getId().equals(next.getMemory().getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            current -> current.updateQuantity(next.getQuantity()),
                            () -> memories.add(next)
                    );
        }
    }

    public void replaceStorages(List<MyBuildStorage> newStorages) {
        storages.removeIf(current -> newStorages.stream()
                .noneMatch(next -> next.getStorage().getId().equals(current.getStorage().getId())));
        for (MyBuildStorage next : newStorages) {
            storages.stream()
                    .filter(current -> current.getStorage().getId().equals(next.getStorage().getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            current -> current.updateQuantity(next.getQuantity()),
                            () -> storages.add(next)
                    );
        }
    }

    public void updateTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Motherboard getMotherboard() { return motherboard; }
    public Gpu getGpu() { return gpu; }
    public Cpu getCpu() { return cpu; }
    public PowerSupply getPowerSupply() { return powerSupply; }
    public CpuCooler getCpuCooler() { return cpuCooler; }
    public PcCase getPcCase() { return pcCase; }
    public List<MyBuildMemory> getMemories() { return List.copyOf(memories); }
    public List<MyBuildStorage> getStorages() { return List.copyOf(storages); }
    public String getName() { return name; }
    public Long getTotalPrice() { return totalPrice; }
    public Boolean getPublicBuild() { return publicBuild; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
