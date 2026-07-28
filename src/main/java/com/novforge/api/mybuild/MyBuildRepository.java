package com.novforge.api.mybuild;

import com.novforge.api.users.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyBuildRepository extends JpaRepository<MyBuild, Long> {

    @EntityGraph(attributePaths = {
            "motherboard", "gpu", "cpu", "powerSupply", "cpuCooler", "pcCase",
            "memories", "memories.memory", "storages", "storages.storage"
    })
    List<MyBuild> findAllByUserOrderByIdDesc(User user);

    @EntityGraph(attributePaths = {
            "motherboard", "gpu", "cpu", "powerSupply", "cpuCooler", "pcCase",
            "memories", "memories.memory", "storages", "storages.storage"
    })
    Optional<MyBuild> findByIdAndUser(Long id, User user);
}
