package com.example.hydra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.hydra.dto.PlantRequest;
import com.example.hydra.dto.PlantData;
import com.example.hydra.service.PlantService;

@RestController 
public class PlantController {
  public final PlantService plantService;
  private static final Logger log = LoggerFactory.getLogger(PlantController.class);

  public PlantController (PlantService plantService) {this.plantService = plantService;}

  @PostMapping("/api/plants/addPlant") 
  public ResponseEntity<String> savePlant(
      @ModelAttribute PlantRequest plantRequest,
      @RequestPart("plantImage") MultipartFile plantImage,
      HttpSession session) {

      log.info("Filename: {}", plantImage.getOriginalFilename()); 
      log.info("Content type: {}", plantImage.getContentType());
      log.info("Image size: {}", plantImage.getSize());

      // get session info and authenticate user
      UUID userId = (UUID) session.getAttribute("userId");
      if(userId == null){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("You are not logged in");
      }
      
      plantService.savePlant(plantRequest, plantImage, userId); 
      return ResponseEntity.ok("Plant Created and Saved");

  }

  // get all plants in user's household
  @GetMapping("/api/plants/getAllPlants")
  public ResponseEntity<?> getAllPlants(HttpSession session) {
    UUID userId = (UUID) session.getAttribute("userId");

    if (userId == null) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body("You are not logged in");
    }

    List<PlantData> plants = plantService.getAllPlants(userId);
    return ResponseEntity.ok(plants);
  }
}
  
