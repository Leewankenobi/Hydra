package com.example.hydra.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.List; 

@Entity
@Table(name = "households")
public class HouseholdEntity {
  // 'property' in schema -> java object attribute 
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID housholdId;

  @Column (name = "name")
  private String housesholdName;

  /* One Household -> Many Plants.
    Uses the 'household' field in PlantEntity to determine
    which plants belong to this household. 
    Cascade operations (save, update, delete) to all associated plants*/
    
  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
  private List<PlantEntity> plants; 

  public UUID getHousholdId() {
    return housholdId;
  }

  public void setHousholdId(UUID housholdId) {
    this.housholdId = housholdId;
  }

  public String getHousesholdName() {
    return housesholdName;
  }

  public void setHousesholdName(String housesholdName) {
    this.housesholdName = housesholdName;
  }

  public List<PlantEntity> getPlants() {
    return plants;
  }

  public void setPlants(List<PlantEntity> plants) {
    this.plants = plants;
  }

 
}
