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
  private UUID householdId;

  @Column (name = "name")
  private String housesholdName;

  @Column(name = "inviteCode")
  private Integer inviteCode;


  /* One Household -> Many Plants.
    Uses the 'household' field in PlantEntity to determine
    which plants belong to this household. 
    Cascade operations (save, update, delete) to all associated plants*/

  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
  private List<PlantEntity> plants; 
  // keep a list of all users
  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL) //#
  private List<UserEntity> users; 

  public UUID getHousholdId() {
    return householdId;
  }

  public void setHousholdId(UUID housholdId) {
    this.householdId = housholdId;
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

  public PlantEntity addPlant(PlantEntity p) {
    plants.add(p);
    p.setHousehold(this); 
    return p; 
  }

   public PlantEntity removePlant(PlantEntity p) {
    plants.remove(p);
    p.setHousehold(null); 
    return p; 
  }

   public List<UserEntity> getUsers() {
    return users;
    
  }

  public UserEntity addUser(UserEntity u) {
    users.add(u);
    u.setHousehold(this); 
    return u;
  }

    public UserEntity removeUser(UserEntity u) {
    users.remove(u);
    u.setHousehold(null); 
    return u;
  }  

  public Integer getinviteCode() {
    return inviteCode; 
  }

  public void setinviteCode(Integer code) {
    this.inviteCode = code; 
  }
}
