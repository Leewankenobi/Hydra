package com.example.hydra.service;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.hydra.dto.PlantRequest;
import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.entity.PlantImageEntity;
import com.example.hydra.repository.PlantRepository;

@Service
public class PlantService {

  private static final long MAX_IMAGE_SIZE = 7 * 1024 * 1024;

  private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
      "image/jpeg",
      "image/png",
      "image/webp"
  );

  private final PlantRepository plantRepository;
  private final UserService userService;

  public PlantService(PlantRepository plantRepository, UserService userService) {
    this.plantRepository = plantRepository;
    this.userService = userService;
  }

  public PlantEntity savePlant(PlantRequest plantRequest, MultipartFile plantImage, UUID userId) {

    validatePlantRequest(plantRequest, userId);
    validatePlantImage(plantImage);

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

    HouseholdEntity household = userService.getHouseholdForUser(userId);
    PlantEntity plantEntity = new PlantEntity();

    plantEntity.setHousehold(household);
    plantEntity.setNickname(plantRequest.getNickname().trim());
    plantEntity.setSpecies(plantRequest.getSpecies());
    plantEntity.setPlantType(plantRequest.getPlantType());
    plantEntity.setMinMoistureLevel(min);
    plantEntity.setMaxMoistureLevel(max);
    plantEntity.setDeviceId(plantRequest.getDeviceId());

    PlantImageEntity imageEntity = new PlantImageEntity();

    try {
      imageEntity.setImageData(plantImage.getBytes());
    } catch (IOException exception) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "The server could not read the plant image",
          exception
      );
    }

    imageEntity.setContentType(plantImage.getContentType());
    imageEntity.setContentSize((int) plantImage.getSize());

    // set both sides because one to one.
    imageEntity.setPlant(plantEntity);
    plantEntity.setImage(imageEntity);

    try {
      return plantRepository.saveAndFlush(plantEntity);
    } catch (DataIntegrityViolationException exception) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Conflict: Could not save plant.", exception
      );
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