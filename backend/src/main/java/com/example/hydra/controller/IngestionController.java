package com.example.hydra.controller;

import com.example.hydra.dto.SensorData;
import com.example.hydra.service.SensorDataService;
import com.example.hydra.service.SensorDataService.DeviceNotPairedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
/**
 * Handles incoming API requests from Sensor
 * IngestionController
 */
public class IngestionController {

  private static final Logger log = LoggerFactory.getLogger(IngestionController.class);

  private final SensorDataService sensorDataService; // one service through out app lifecycle

  public IngestionController(SensorDataService sensorDataService) {
    this.sensorDataService = sensorDataService;
  }

  @PostMapping("/api/sensor") // endpoint to receive sensor data
  /**
   * Response entity repesents entire http response,
   * the body returned is of type string.
   * Sensor dto is deserialized automatically by jackson
   */
  public ResponseEntity<String> receiveReading(@RequestBody SensorData sensorData) {
    try {
      log.debug("Received sensor payload: {}", sensorData);
      sensorDataService.validateSensorData(sensorData);
      sensorDataService.saveSensorData(sensorData);
      return ResponseEntity.status(HttpStatus.OK).body("Saved Reading!");

    } catch (IllegalArgumentException e) { // 400
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());

    } catch (DeviceNotPairedException e) { // 404
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(e.getMessage());

    } catch (DataAccessException e) { // 500
      log.error("Database error while saving sensor reading", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Could not save reading");

    } catch (Exception e) { // 500
      log.error("Unexpected error while saving sensor reading", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Unexpected server error");
    }
  }
}
