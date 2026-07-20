package com.example.hydra.repository;
import com.example.hydra.entity.HouseholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HouseholdRepository extends JpaRepository<HouseholdEntity, UUID> {
}
