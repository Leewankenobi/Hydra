package com.example.hydra.service;

import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.repository.HouseholdRepository;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    } while (householdRepository.findByInviteCode(code).isPresent());

    try {

      HouseholdEntity householdEntity = new HouseholdEntity();
      householdEntity.setHousesholdName(name);
      householdEntity.setInviteCode(code);
      HouseholdEntity savedEntity = householdRepository.save(householdEntity); // returned
      return savedEntity;

    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while creating household");  
    }
  }
}
