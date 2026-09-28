package com.example.hydra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.hydra.dto.PlantRequest;
import com.example.hydra.service.PlantService;

@RestController 
public class PlantController {
  public final PlantService plantService;

  public PlantController (PlantService plantService) {this.plantService = plantService;}

  @PostMapping("/api/plants/addPlant") 
  public ResponseEntity<String> savePlant(@RequestBody PlantRequest plantRequest ) {
    // jackson automatically deserialized json and saves it to DTO
    plantService.savePlant(plantRequest); 

    
  }
  
}
