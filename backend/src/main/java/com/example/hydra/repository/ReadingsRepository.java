package com.example.hydra.repository;
import com.example.hydra.entity.ReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReadingsRepository extends JpaRepository<ReadingEntity, UUID> {
}
