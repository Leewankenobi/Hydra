package com.example.hydra.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.OffsetDateTime;

@Entity
public class WateringLogsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "log_id", nullable = false)
  private UUID log_id;

  @ManyToOne // many watering logs to one plant
  @JoinColumn(name = "plant_id", nullable = false)
  private PlantEntity plantEntity;
  
  @Column(name = "watered_at")
  private OffsetDateTime timestamp;   
  
  public UUID getLogId() {
    return log_id;
  }

  public void setLogId(UUID logId) {
    this.log_id = logId;
  }

  public PlantEntity getPlantEntity() {
    return plantEntity;
  }

  public void setPlantEntity(PlantEntity plantEntity) {
    this.plantEntity = plantEntity;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
