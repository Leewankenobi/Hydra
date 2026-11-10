package com.example.hydra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;
import java.time.OffsetDateTime;

@Entity
@Table(name = "readings")
public class ReadingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "reading_id", nullable = false)
  private UUID reading_id; 

  @ManyToOne // there will be multiple readings for a plant
  // map this reading to its plant entity via the plant_id column
  @JoinColumn(name = "plant_id", nullable = false)
  private PlantEntity plantEntity; 

  @Column(name = "recorded_at", nullable = false)
  private OffsetDateTime recordedAt; // use Zoned date time if this doesnt work

  @Column(name = "moisture_reading", nullable = false)
  private Integer moisture_reading; 

  @Column(name = "device_id", nullable = false)
  private String deviceId;

  @Column(name = "temperature")
  private Double temperature;

  public UUID getReading_id() {
    return reading_id;
  }

  public void setReading_id(UUID reading_id) {
    this.reading_id = reading_id;
  }

  public PlantEntity getPlantEntity() {
    return plantEntity;
  }

  public void setPlantEntity(PlantEntity plantEntity) {
    this.plantEntity = plantEntity;
  }

  public OffsetDateTime getRecordedAt() {
    return recordedAt;
  }

  public void setRecordedAt(OffsetDateTime recordedAt) {
    this.recordedAt = recordedAt;
  }

  public Integer getMoisture_reading() {
    return moisture_reading;
  }

  public void setMoisture_reading(Integer moisture_reading) {
    this.moisture_reading = moisture_reading;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public String toString() {
    return " Reading ID: " + getReading_id() +
     " Plant ID: " + getPlantEntity().getPlantId() + 
     " Device ID: " + getDeviceId() +
     " Recorded at: " + getRecordedAt() + 
     " Moisture Reading: " + getMoisture_reading() +
     " Temperature: " + getTemperature();
  }
}
 