package com.example.hydra.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hydra.dto.SensorData;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.entity.ReadingEntity;
import com.example.hydra.entity.UserEntity;
import com.example.hydra.repository.ReadingsRepository;
import com.example.hydra.repository.PlantRepository;
import com.example.hydra.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SensorDataService {

  private static final Logger log = LoggerFactory.getLogger(SensorDataService.class);

  private final ReadingsRepository readingRepository;
  private final PlantRepository plantRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  public SensorDataService(
      ReadingsRepository readingsRepository,
      PlantRepository plantRepository,
      UserRepository userRepository,
      NotificationService notificationService) {
    this.readingRepository = readingsRepository;
    this.plantRepository = plantRepository;
    this.userRepository = userRepository;
    this.notificationService = notificationService;
  }

  public void validateSensorData(SensorData data) {
    // wrong data types are handled by Spring
    if (data.getDeviceId() == null || data.getDeviceId().isBlank()) {
      throw new IllegalArgumentException("Device ID is required");
    }
    if (data.getMoistureLevel() == null) {
      throw new IllegalArgumentException("Moisture level is required");
    }
    if (data.getMoistureLevel() < 200 || data.getMoistureLevel() > 2000) {
      throw new IllegalArgumentException("Moisture level should be within 200 to 2000 range");
    }
  }

  @Transactional
  public void saveSensorData(SensorData data) {
    String deviceId = PlantEntity.normalizeDeviceId(data.getDeviceId());

    // The sensor identifies itself; the plant is resolved from the pairing.
    PlantEntity plant = plantRepository.findByDeviceId(deviceId)
        .orElseThrow(() -> new DeviceNotPairedException(
            "No plant is paired with device " + deviceId));

    // Fetch the previous reading before saving the new one so the next query
    // compares against the reading immediately before this one.
    ReadingEntity previousReading = readingRepository
        .findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId())
        .orElse(null);

    ReadingEntity reading = new ReadingEntity();
    reading.setPlantEntity(plant);
    reading.setDeviceId(plant.getDeviceId());
    reading.setMoisture_reading(data.getMoistureLevel());
    reading.setTemperature(data.getTemperature());
    reading.setRecordedAt(OffsetDateTime.now());
    readingRepository.save(reading);

    try {
      notifyIfLowMoisture(plant, reading, previousReading);
    } catch (RuntimeException e) {
      // Notification problems must never fail the sensor request.
      log.error("Could not queue low-moisture notification for plant {}",
          plant.getPlantId(), e);
    }

    log.debug("Saved reading for plant {} from device {}", plant.getPlantId(), deviceId);
  }

  private void notifyIfLowMoisture(
      PlantEntity plant,
      ReadingEntity reading,
      ReadingEntity previousReading) {

    Integer minMoisture = plant.getMinMoistureLevel();
    if (minMoisture == null) {
      return;
    }

    int currentPercent = reading.getMoisturePercent();
    if (currentPercent >= minMoisture) {
      return;
    }

    boolean wasAlreadyLow = previousReading != null
        && previousReading.getMoisturePercent() < minMoisture;
    if (wasAlreadyLow) {
      return;
    }

    List<String> recipients = userRepository.findByHousehold(plant.getHousehold())
        .stream()
        .map(UserEntity::getEmail)
        .filter(email -> email != null && !email.isBlank())
        .toList();

    if (recipients.isEmpty()) {
      log.warn("No household email recipients for plant {}", plant.getPlantId());
      return;
    }

    notificationService.sendLowMoistureEmail(
        recipients,
        plant.getNickname(),
        currentPercent,
        minMoisture,
        reading.getRecordedAt());
  }

  /**
   * The sensor's device is not paired with any plant. The controller maps this
   * to a 404; it is a domain outcome, not a malformed request.
   */
  public static class DeviceNotPairedException extends RuntimeException {

    public DeviceNotPairedException(String message) {
      super(message);
    }
  }
}
