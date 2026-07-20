package com.example.hydra.repository;
import com.example.hydra.entity.WateringLogsEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WateringLogsRepository extends JpaRepository<WateringLogsEntity, UUID> {
}
