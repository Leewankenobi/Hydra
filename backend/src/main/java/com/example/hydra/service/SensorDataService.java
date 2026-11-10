package com.example.hydra.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hydra.dto.SensorData;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.entity.ReadingEntity;
import com.example.hydra.repository.ReadingsRepository;
import com.example.hydra.repository.PlantRepository;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SensorDataService {

  private static final Logger log = LoggerFactory.getLogger(SensorDataService.class);

  private final ReadingsRepository readingRepository;
  private final PlantRepository plantRepository;

  // inject both repositories
  public SensorDataService(ReadingsRepository readingsRepository, PlantRepository plantRepository) {
    this.readingRepository = readingsRepository;
    this.plantRepository = plantRepository;
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

    ReadingEntity reading = new ReadingEntity();
    reading.setPlantEntity(plant);
    reading.setDeviceId(plant.getDeviceId());
    reading.setMoisture_reading(data.getMoistureLevel());
    reading.setTemperature(data.getTemperature());
    reading.setRecordedAt(OffsetDateTime.now());
    readingRepository.save(reading);

    log.debug("Saved reading for plant {} from device {}", plant.getPlantId(), deviceId);
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
