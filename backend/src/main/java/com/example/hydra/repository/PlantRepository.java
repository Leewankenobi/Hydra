package com.example.hydra.repository;
import com.example.hydra.entity.PlantEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlantRepository extends JpaRepository<PlantEntity, UUID> {
}
