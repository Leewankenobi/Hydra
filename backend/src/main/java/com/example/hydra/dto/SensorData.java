package com.example.hydra.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

// Reading entity
public class SensorData {

  @JsonProperty("plant_id")
  private UUID plantId;

  @JsonProperty("moisture")
  private Integer moistureLevel;

  @JsonProperty("temperature")
  private Double temperature;

  public SensorData() {
  }

  public UUID getPlantId() {
    return plantId;
  }

  public void setPlantId(UUID plantId) {
    this.plantId = plantId;
  }

  public Integer getMoistureLevel() {
    return moistureLevel;
  }

  public void setMoistureLevel(Integer moistureLevel) {
    this.moistureLevel = moistureLevel;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public String toString() {
    return " Plant ID: " + getPlantId() + " Mositure Level: " + getMoistureLevel() + " Temperature: " + temperature;
  }
} 