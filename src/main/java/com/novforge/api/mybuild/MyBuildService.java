package com.novforge.api.mybuild;

import com.novforge.api.equipment.cpu.Cpu;
import com.novforge.api.equipment.cpu.CpuRepository;
import com.novforge.api.equipment.cpucooler.CpuCooler;
import com.novforge.api.equipment.cpucooler.CpuCoolerRepository;
import com.novforge.api.equipment.gpu.Gpu;
import com.novforge.api.equipment.gpu.GpuRepository;
import com.novforge.api.equipment.memory.Memory;
import com.novforge.api.equipment.memory.MemoryRepository;
import com.novforge.api.equipment.motherboard.Motherboard;
import com.novforge.api.equipment.motherboard.MotherboardRepository;
import com.novforge.api.equipment.pccase.PcCase;
import com.novforge.api.equipment.pccase.PcCaseRepository;
import com.novforge.api.equipment.powersupply.PowerSupply;
import com.novforge.api.equipment.powersupply.PowerSupplyRepository;
import com.novforge.api.equipment.storage.Storage;
import com.novforge.api.equipment.storage.StorageRepository;
import com.novforge.api.mybuild.dto.MyBuildCreateRequest;
import com.novforge.api.mybuild.dto.MyBuildResponse;
import com.novforge.api.mybuild.dto.MyBuildUpdateRequest;
import com.novforge.api.users.User;
import com.novforge.api.users.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class MyBuildService {

    private final MyBuildRepository myBuildRepository;
    private final UserRepository userRepository;
    private final MotherboardRepository motherboardRepository;
    private final GpuRepository gpuRepository;
    private final CpuRepository cpuRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final CpuCoolerRepository cpuCoolerRepository;
    private final PcCaseRepository pcCaseRepository;
    private final MemoryRepository memoryRepository;
    private final StorageRepository storageRepository;

    public MyBuildService(
            MyBuildRepository myBuildRepository,
            UserRepository userRepository,
            MotherboardRepository motherboardRepository,
            GpuRepository gpuRepository,
            CpuRepository cpuRepository,
            PowerSupplyRepository powerSupplyRepository,
            CpuCoolerRepository cpuCoolerRepository,
            PcCaseRepository pcCaseRepository,
            MemoryRepository memoryRepository,
            StorageRepository storageRepository
    ) {
        this.myBuildRepository = myBuildRepository;
        this.userRepository = userRepository;
        this.motherboardRepository = motherboardRepository;
        this.gpuRepository = gpuRepository;
        this.cpuRepository = cpuRepository;
        this.powerSupplyRepository = powerSupplyRepository;
        this.cpuCoolerRepository = cpuCoolerRepository;
        this.pcCaseRepository = pcCaseRepository;
        this.memoryRepository = memoryRepository;
        this.storageRepository = storageRepository;
    }

    public List<MyBuildResponse> findAll(Jwt jwt) {
        User user = findUser(jwt);
        return myBuildRepository.findAllByUserOrderByIdDesc(user).stream()
                .map(MyBuildResponse::from)
                .toList();
    }

    public MyBuildResponse findById(Jwt jwt, Long buildId) {
        return MyBuildResponse.from(findOwnedBuild(findUser(jwt), buildId));
    }

    @Transactional
    public MyBuildResponse create(Jwt jwt, MyBuildCreateRequest request) {
        MyBuild build = new MyBuild(findUser(jwt), request.buildName(), request.publicBuild());
        build.updateParts(
                findMotherboard(request.motherboardId()),
                findGpu(request.gpuId()),
                findCpu(request.cpuId()),
                findPowerSupply(request.powerId()),
                findCpuCooler(request.cpuCoolerId()),
                findPcCase(request.caseId())
        );
        build.replaceMemories(toMemories(build, request.memories()));
        build.replaceStorages(toStorages(build, request.storages()));
        build.updateTotalPrice(calculateTotalPrice(build));
        return MyBuildResponse.from(myBuildRepository.saveAndFlush(build));
    }

    @Transactional
    public MyBuildResponse update(Jwt jwt, Long buildId, MyBuildUpdateRequest request) {
        MyBuild build = findOwnedBuild(findUser(jwt), buildId);
        build.updateBasicInfo(request.buildName(), request.publicBuild());
        build.updateParts(
                request.motherboardId() == null ? build.getMotherboard() : findMotherboard(request.motherboardId()),
                request.gpuId() == null ? build.getGpu() : findGpu(request.gpuId()),
                request.cpuId() == null ? build.getCpu() : findCpu(request.cpuId()),
                request.powerId() == null ? build.getPowerSupply() : findPowerSupply(request.powerId()),
                request.cpuCoolerId() == null ? build.getCpuCooler() : findCpuCooler(request.cpuCoolerId()),
                request.caseId() == null ? build.getPcCase() : findPcCase(request.caseId())
        );
        if (request.memories() != null) {
            build.replaceMemories(toMemories(build, request.memories()));
        }
        if (request.storages() != null) {
            build.replaceStorages(toStorages(build, request.storages()));
        }
        build.updateTotalPrice(calculateTotalPrice(build));
        return MyBuildResponse.from(myBuildRepository.saveAndFlush(build));
    }

    @Transactional
    public void delete(Jwt jwt, Long buildId) {
        myBuildRepository.delete(findOwnedBuild(findUser(jwt), buildId));
    }

    private User findUser(Jwt jwt) {
        try {
            return userRepository.findById(Long.valueOf(jwt.getSubject()))
                    .orElseThrow(() -> new MyBuildNotFoundException(-1L));
        } catch (NumberFormatException exception) {
            throw new InvalidBuildPartException("Access Token의 사용자 ID가 올바르지 않습니다.");
        }
    }

    private MyBuild findOwnedBuild(User user, Long buildId) {
        return myBuildRepository.findByIdAndUser(buildId, user)
                .orElseThrow(() -> new MyBuildNotFoundException(buildId));
    }

    private Motherboard findMotherboard(Long id) {
        if (id == null) return null;
        return motherboardRepository.findById(id)
                .orElseThrow(() -> new InvalidBuildPartException("메인보드", id));
    }

    private Gpu findGpu(Long id) {
        if (id == null) return null;
        return gpuRepository.findById(id)
                .orElseThrow(() -> new InvalidBuildPartException("GPU", id));
    }

    private Cpu findCpu(Long id) {
        if (id == null) return null;
        return cpuRepository.findById(id)
                .orElseThrow(() -> new InvalidBuildPartException("CPU", id));
    }

    private PowerSupply findPowerSupply(Long id) {
        if (id == null) return null;
        return powerSupplyRepository.findById(id)
                .orElseThrow(() -> new InvalidBuildPartException("파워 서플라이", id));
    }

    private CpuCooler findCpuCooler(Long id) {
        if (id == null) return null;
        return cpuCoolerRepository.findById(id)
                .orElseThrow(() -> new InvalidBuildPartException("CPU 쿨러", id));
    }

    private PcCase findPcCase(Long id) {
        if (id == null) return null;
        return pcCaseRepository.findById(id)
                .orElseThrow(() -> new InvalidBuildPartException("케이스", id));
    }

    private List<MyBuildMemory> toMemories(
            MyBuild build,
            List<MyBuildCreateRequest.PartQuantityRequest> requests
    ) {
        if (requests == null) return List.of();
        validateNoDuplicates(requests, "메모리");
        return requests.stream()
                .map(request -> new MyBuildMemory(
                        build,
                        memoryRepository.findById(request.id())
                                .orElseThrow(() -> new InvalidBuildPartException("메모리", request.id())),
                        request.quantity()))
                .toList();
    }

    private List<MyBuildStorage> toStorages(
            MyBuild build,
            List<MyBuildCreateRequest.PartQuantityRequest> requests
    ) {
        if (requests == null) return List.of();
        validateNoDuplicates(requests, "보조기억장치");
        return requests.stream()
                .map(request -> new MyBuildStorage(
                        build,
                        storageRepository.findById(request.id())
                                .orElseThrow(() -> new InvalidBuildPartException("보조기억장치", request.id())),
                        request.quantity()))
                .toList();
    }

    private void validateNoDuplicates(
            List<MyBuildCreateRequest.PartQuantityRequest> requests,
            String part
    ) {
        Set<Long> ids = new HashSet<>();
        if (requests.stream().anyMatch(request -> !ids.add(request.id()))) {
            throw new InvalidBuildPartException(part + " ID를 중복해서 입력할 수 없습니다.");
        }
    }

    private long calculateTotalPrice(MyBuild build) {
        long total = 0L;
        total = addPrice(total, build.getMotherboard() == null ? null : build.getMotherboard().getPrice());
        total = addPrice(total, build.getGpu() == null ? null : build.getGpu().getPrice());
        total = addPrice(total, build.getCpu() == null ? null : build.getCpu().getPrice());
        total = addPrice(total, build.getPowerSupply() == null ? null : build.getPowerSupply().getPrice());
        total = addPrice(total, build.getCpuCooler() == null ? null : build.getCpuCooler().getPrice());
        total = addPrice(total, build.getPcCase() == null ? null : build.getPcCase().getPrice());
        for (MyBuildMemory item : build.getMemories()) {
            total = Math.addExact(total, Math.multiplyExact(item.getMemory().getPrice(), item.getQuantity().longValue()));
        }
        for (MyBuildStorage item : build.getStorages()) {
            total = Math.addExact(total, Math.multiplyExact(item.getStorage().getPrice(), item.getQuantity().longValue()));
        }
        return total;
    }

    private long addPrice(long total, Long price) {
        return price == null ? total : Math.addExact(total, price);
    }
}
