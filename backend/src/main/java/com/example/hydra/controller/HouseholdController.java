package com.example.hydra.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import com.example.hydra.service.HouseholdService;

@RestController 
public class HouseholdController {

  private final HouseholdService householdService;

  public HouseholdController(HouseholdService householdService){ 
    this.householdService = householdService;
  }

  @PostMapping ("api/households/validateCode")
  public ResponseEntity<String> validateCode(@RequestBody Integer joinCode){
      return householdService.validateCode(joinCode);
  }  
}
