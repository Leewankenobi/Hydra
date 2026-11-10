package com.example.hydra.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "plants")
public class PlantEntity { 
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "plant_id")
  private UUID plantId;

  /* houshold id is fk in schema; 
    There is a household_id column in the plants table,
    and JPA uses that column to connect this PlantEntity
    to a HouseholdEntity object." */

  @ManyToOne (fetch = FetchType.LAZY) // only fetch houshold when code accesses it
  @JoinColumn(name = "household_id", nullable = false) 
  private HouseholdEntity household; 

  @Column(name = "species")
  private String species; 

  @Column(name = "nickname", nullable = false)
  private String nickname; 

  @Column(name = "plant_type") 
  private String plantType; 

  @Column(name = "min_moisture_level")
  private Integer minMoistureLevel; 

  @Column(name = "max_moisture_level")
  private Integer maxMoistureLevel;
  
  // pico id - one pico/plant
  @Column(name = "device_id", nullable = false, unique = true)
  private String deviceId;

  @Column(name = "image_key")
  private String image_key; 

  public UUID getPlantId() {
    return plantId;
  }

  public void setPlantId(UUID plantId) {
    this.plantId = plantId;
  }

  public HouseholdEntity getHousehold() {
    return household;
  }

  public void setHousehold(HouseholdEntity household) {
    this.household = household;
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

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public void setImageKey(String key) {
    this.image_key = key;
  }

  public String getImageKey(){
    return image_key;
  }

}
