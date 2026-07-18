package com.example.hydra.dto;

import java.util.UUID;

public class SensorData {

  private String plant_id;
  private Double moistureLevel;
  private Double temperature;

  public SensorData() {
  }

  public String getPlantId() {
    return plant_id;
  }

  public void setPlantId(String plantId) {
    this.plant_id = plantId;
  }

  public Double getMoistureLevel() {
    return moistureLevel;
  }

  public void setMoistureLevel(Double moistureLevel) {
    this.moistureLevel = moistureLevel;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }
}