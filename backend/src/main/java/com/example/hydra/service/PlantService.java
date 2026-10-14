package com.example.hydra.service;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.hydra.dto.PlantRequest;
import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.dto.PlantData;
import com.example.hydra.repository.PlantRepository;
import com.example.hydra.service.ImageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlantService {

  private static final long MAX_IMAGE_SIZE = 7 * 1024 * 1024;
   private static final Logger log = LoggerFactory.getLogger(PlantService.class);

  private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
    "image/jpeg",
    "image/png",
    "image/webp");

  private final PlantRepository plantRepository;
  private final UserService userService;
  private final ImageService imageService;

  public PlantService(PlantRepository plantRepository, UserService userService, ImageService imageService) {
    this.plantRepository = plantRepository;
    this.userService = userService;
    this.imageService = imageService;
  }

  public ResponseEntity<String> savePlant(PlantRequest plantRequest, MultipartFile plantImage, UUID userId) {

    validatePlantRequest(plantRequest, userId);
    validatePlantImage(plantImage);

    // save to household  
    PlantEntity plantEntity = new PlantEntity();
    HouseholdEntity household = userService.getHouseholdForUser(userId);
    plantEntity.setHousehold(household);

    plantEntity.setNickname(plantRequest.getNickname().trim());
    plantEntity.setSpecies(plantRequest.getSpecies());
    plantEntity.setPlantType(plantRequest.getPlantType());

    plantEntity.setMinMoistureLevel(plantRequest.getMinMoistureLevel());
    plantEntity.setMaxMoistureLevel(plantRequest.getMaxMoistureLevel());
    plantEntity.setDeviceId(plantRequest.getDeviceId());

    // upload image and save key
    String key = imageService.uploadImage(plantImage);
    plantEntity.setImageKey(key);

    // save to database
    // if saving fails, delete orphan image in filebase
    try {
      plantRepository.saveAndFlush(plantEntity);
      return ResponseEntity.ok("Plant saved sucessfully!");

    } catch (DataIntegrityViolationException exception) {
      cleanUpImage(key);
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Conflict: Could not save plant: ", exception
      );
    } catch(RuntimeException e) {
      cleanUpImage(key);
      throw e; 
    }
  }

  @Transactional(readOnly = true)
  public List<PlantData> getAllPlants(UUID userId){
    // session is already verified by controller

    HouseholdEntity household = userService.getHouseholdForUser(userId);
    List<PlantEntity> plants = plantRepository.findByHousehold(household);
    List<PlantData> result = new ArrayList<>();

    for(PlantEntity plant: plants) {
      String imageUrl = null;
      // get pre signed URL
      if(plant.getImageKey() != null){
        imageUrl = imageService.getImageURL(plant.getImageKey());
      }

      // fill in the dto
      PlantData data = new PlantData(
        plant.getPlantId(),
        plant.getNickname(),
        plant.getSpecies(),
        plant.getPlantType(),
        plant.getMinMoistureLevel(),
        plant.getMaxMoistureLevel(),
        plant.getDeviceId(),
        imageUrl
      );

      result.add(data);
    }

    return result; 
  }


  // --------- Helpers --------- //

  private void cleanUpImage(String key) {
    try {
      imageService.deleteImage(key);
    } catch (RuntimeException ignored) {
      // best effort: the original save error matters more
      }
  }

  private void validatePlantRequest(PlantRequest plantRequest, UUID userId) {

    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You must be logged in");
    }

    if (plantRequest == null ||
        plantRequest.getNickname() == null ||
        plantRequest.getNickname().isBlank() ||
        plantRequest.getMinMoistureLevel() == null ||
        plantRequest.getMaxMoistureLevel() == null ||
        plantRequest.getPlantType() == null ||
        plantRequest.getDeviceId() == null ||
        plantRequest.getDeviceId().isBlank()) {

      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Please provide all required plant details"
      );
    }

    int min = plantRequest.getMinMoistureLevel();
    int max = plantRequest.getMaxMoistureLevel();

    if (min < 0 || min > 100 ||
        max < 0 || max > 100 ||
        min >= max) {

      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Moisture levels must be between 0 and 100, and minimum must be lower than maximum"
      );
    }
  }

  private void validatePlantImage(MultipartFile plantImage) {

    if (plantImage == null || plantImage.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A plant image is required");
    }

    if (plantImage.getSize() > MAX_IMAGE_SIZE) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Plant image must be smaller than 7 MB"
      );
    }

    String contentType = plantImage.getContentType();

    if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {

      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Only JPEG, PNG, and WebP images are supported"
      );
    }
  }
}