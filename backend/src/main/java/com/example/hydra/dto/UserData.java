package com.example.hydra.dto;

public class UserData {

  private String household_name; 
  private String first_name;
  private String last_name;
  private String email;
  private String username; 
  private String passwordHash;
  private Integer joinCode; // code to join a household, not to invite

  public UserData() {}

  public String getHousehold_name() {
    return household_name;
  }

  public void setHousehold_name(String household_name) {
    this.household_name = household_name;
  }

  public String getFirst_name() {
    return first_name;
  }

  public void setFirst_name(String first_name) {
    this.first_name = first_name;
  }

  public String getLast_name() {
    return last_name;
  }

  public void setLast_name(String last_name) {
    this.last_name = last_name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordhash) {
    this.passwordHash = passwordhash;
  }
  
  public void setJoinCode(Integer joinCode) {
    this.joinCode = joinCode;
  }

  public Integer getJoinCode() {
    return joinCode;
  }
}
