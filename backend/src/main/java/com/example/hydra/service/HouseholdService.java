package com.example.hydra.service;

import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.repository.HouseholdRepository;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;
import java.util.UUID;

@Service
public class HouseholdService {
  public final HouseholdRepository householdRepository;
  public HouseholdService(HouseholdRepository householdRepository) {
    this.householdRepository = householdRepository;
  }

  public HouseholdEntity createHousehold(String name){

    // keep generating unique codes if current one is already taken
    int code; 
    do {
      code = ThreadLocalRandom.current().nextInt(100000, 1000000);
    } while (householdRepository.findByinviteCode(code).isPresent());

    try {

      HouseholdEntity householdEntity = new HouseholdEntity();
      householdEntity.setHouseholdName(name);
      householdEntity.setinviteCode(code);
      HouseholdEntity savedEntity = householdRepository.save(householdEntity); // returned
      return savedEntity;

    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while creating household");  
    }
  }

  public ResponseEntity<String> validateCode(Integer code) {

    if (code == null) { //400
    return ResponseEntity.badRequest().body("Join code is required");
}
    // find household by code // 200
    if(householdRepository.findByinviteCode(code).isPresent()) {
      return ResponseEntity.ok("Valid code");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
        .body("Invalid code"); 

  }

  public HouseholdEntity getHouseholdByCode(Integer joinCode){
     Optional<HouseholdEntity> household = householdRepository.findByinviteCode(joinCode);

    if(household.isPresent()){
      return household.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hosuehold not found");
  }  
}
