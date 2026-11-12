package com.example.hydra.repository;
import com.example.hydra.entity.ReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingsRepository extends JpaRepository<ReadingEntity, UUID> {

  Optional<ReadingEntity> findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(UUID plantId);

  @Modifying
  @Query("delete from ReadingEntity r where r.plantEntity.plantId = :plantId")
  void deleteByPlantId(@Param("plantId") UUID plantId);
}
