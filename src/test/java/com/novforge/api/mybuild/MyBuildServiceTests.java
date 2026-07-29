package com.novforge.api.mybuild;

import com.novforge.api.equipment.memory.Memory;
import com.novforge.api.equipment.memory.MemoryRepository;
import com.novforge.api.equipment.memory.dto.MemoryCreateRequest;
import com.novforge.api.equipment.motherboard.Motherboard;
import com.novforge.api.equipment.motherboard.MotherboardRepository;
import com.novforge.api.equipment.motherboard.dto.MotherboardCreateRequest;
import com.novforge.api.equipment.storage.Storage;
import com.novforge.api.equipment.storage.StorageRepository;
import com.novforge.api.equipment.storage.dto.StorageCreateRequest;
import com.novforge.api.mybuild.dto.MyBuildCreateRequest;
import com.novforge.api.mybuild.dto.MyBuildResponse;
import com.novforge.api.mybuild.dto.MyBuildUpdateRequest;
import com.novforge.api.users.User;
import com.novforge.api.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MyBuildServiceTests {

    @Autowired
    private MyBuildService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MotherboardRepository motherboardRepository;

    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Test
    void createsUpdatesAndReadsOnlyOwnedBuild() {
        User owner = userRepository.save(new User(
                "google-owner", "Owner", "owner", "owner@example.com", null));
        User anotherUser = userRepository.save(new User(
                "google-other", "Other", "other", "other@example.com", null));

        Motherboard motherboard = motherboardRepository.save(new Motherboard(new MotherboardCreateRequest(
                "ASUS", "B650", 100_000L, "AM5", "B650", "ATX", "DDR5",
                4, 192, 8000, "PCIe 5.0", 2, 3, 4, true, true, null, null
        )));
        Memory memory = memoryRepository.save(new Memory(new MemoryCreateRequest(
                "Samsung", "DDR5 16GB", 50_000L, "DDR5", 16, 5600,
                1, 16, "DIMM", 46, new BigDecimal("1.10"), false, null, null
        )));
        Storage storage = storageRepository.save(new Storage(new StorageCreateRequest(
                "Samsung", "NVMe 1TB", 70_000L, "SSD", "PCIe 4.0",
                1000, 7000, "M.2 2280", 1024, null, null
        )));

        Jwt ownerJwt = jwt(owner.getId());
        MyBuildResponse created = service.create(ownerJwt, new MyBuildCreateRequest(
                "게이밍 견적",
                motherboard.getId(),
                null,
                null,
                null,
                null,
                null,
                List.of(new MyBuildCreateRequest.PartQuantityRequest(memory.getId(), 2)),
                List.of(new MyBuildCreateRequest.PartQuantityRequest(storage.getId(), 1)),
                false
        ));

        assertThat(created.totalPrice()).isEqualTo(270_000L);
        assertThat(created.memories()).singleElement().extracting(MyBuildResponse.PartQuantityResponse::quantity)
                .isEqualTo(2);
        assertThat(service.findAll(ownerJwt)).hasSize(1);

        MyBuildResponse updated = service.update(ownerJwt, created.buildId(), new MyBuildUpdateRequest(
                "수정 견적", null, null, null, null, null, null,
                List.of(new MyBuildCreateRequest.PartQuantityRequest(memory.getId(), 4)),
                List.of(),
                true
        ));

        assertThat(updated.buildName()).isEqualTo("수정 견적");
        assertThat(updated.totalPrice()).isEqualTo(300_000L);
        assertThat(updated.storages()).isEmpty();
        assertThat(updated.publicBuild()).isTrue();

        MyBuildResponse partRemoved = service.removeSinglePart(
                ownerJwt,
                created.buildId(),
                MyBuildSinglePartType.MOTHERBOARD
        );

        assertThat(partRemoved.motherboard()).isNull();
        assertThat(partRemoved.totalPrice()).isEqualTo(200_000L);

        assertThatThrownBy(() -> service.removeSinglePart(
                jwt(anotherUser.getId()),
                created.buildId(),
                MyBuildSinglePartType.CPU
        )).isInstanceOf(MyBuildNotFoundException.class);

        assertThatThrownBy(() -> service.findById(jwt(anotherUser.getId()), created.buildId()))
                .isInstanceOf(MyBuildNotFoundException.class);
    }

    private Jwt jwt(Long userId) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(userId.toString())
                .build();
    }
}
