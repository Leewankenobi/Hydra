package com.example.hydra.dto;

import java.util.UUID;

public class PlantData {

  private UUID plantId;
  private String nickname;
  private String species;
  private String plantType;
  private Integer minMoistureLevel;
  private Integer maxMoistureLevel;
  private String deviceId;
  private String imageUrl; // not key!

  public PlantData() {}

  public PlantData(
      UUID plantId,
      String nickname,
      String species,
      String plantType,
      Integer minMoistureLevel,
      Integer maxMoistureLevel,
      String deviceId,
      String imageUrl
  ) {
    this.plantId = plantId;
    this.nickname = nickname;
    this.species = species;
    this.plantType = plantType;
    this.minMoistureLevel = minMoistureLevel;
    this.maxMoistureLevel = maxMoistureLevel;
    this.deviceId = deviceId;
    this.imageUrl = imageUrl;
  }

  public UUID getPlantId() {
    return plantId;
  }

  public void setPlantId(UUID plantId) {
    this.plantId = plantId;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public String getPlantType() {
    return plantType;
  }

  public void setPlantType(String plantType) {
    this.plantType = plantType;
  }

  public Integer getMinMoistureLevel() {
    return minMoistureLevel;
  }

  public void setMinMoistureLevel(Integer minMoistureLevel) {
    this.minMoistureLevel = minMoistureLevel;
  }

  public Integer getMaxMoistureLevel() {
    return maxMoistureLevel;
  }

  public void setMaxMoistureLevel(Integer maxMoistureLevel) {
    this.maxMoistureLevel = maxMoistureLevel;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}