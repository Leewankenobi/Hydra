package com.example.hydra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Reading entity
public class SensorData {

  @JsonProperty("device_id")
  private String deviceId;

  @JsonProperty("moisture")
  private Integer moistureLevel;

  @JsonProperty("temperature")
  private Double temperature;

  public SensorData() {
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
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

  @Override
  public String toString() {
    return "Device ID: " + deviceId
        + " Moisture Level: " + moistureLevel
        + " Temperature: " + temperature;
  }
}
