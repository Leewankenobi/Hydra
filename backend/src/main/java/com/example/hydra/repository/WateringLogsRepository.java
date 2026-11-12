package com.example.hydra.repository;
import com.example.hydra.entity.WateringLogsEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WateringLogsRepository extends JpaRepository<WateringLogsEntity, UUID> {

  @Modifying
  @Query("delete from WateringLogsEntity w where w.plantEntity.plantId = :plantId")
  void deleteByPlantId(@Param("plantId") UUID plantId);
}
