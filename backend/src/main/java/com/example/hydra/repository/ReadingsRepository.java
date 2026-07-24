package com.example.hydra.repository;
import com.example.hydra.entity.ReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReadingsRepository extends JpaRepository<ReadingEntity, UUID> {
}
