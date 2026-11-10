package com.example.hydra.repository;
import com.example.hydra.entity.PlantEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import com.example.hydra.entity.HouseholdEntity;

@Repository
public interface PlantRepository extends JpaRepository<PlantEntity, UUID> {

  List<PlantEntity> findByHousehold(HouseholdEntity household);

  Optional<PlantEntity> findByDeviceId(String deviceId);

  boolean existsByDeviceId(String deviceId);
}
