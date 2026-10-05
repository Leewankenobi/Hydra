package com.example.hydra.dto;
import com.example.hydra.dto.PlantRequest;

public class PlantRequest {

  private String householdId;
  private String species;
  private String nickname;
  private Integer minMoistureLevel;
  private Integer maxMoistureLevel;
  private String plantType; 
  private String deviceID;

  public String getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
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

  public void setDeviceId(String deviceID){
    this.deviceID = deviceID;
  }

  public String getDeviceId() {
    return deviceID;
  }
}
