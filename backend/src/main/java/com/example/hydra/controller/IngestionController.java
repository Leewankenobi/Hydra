package com.example.hydra.controller;

import com.example.hydra.dto.sensorData;
import com.example.hydra.service.sensorDataService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController  
public class IngestionController {

  private final sensorDataService sensorDataService; // one service through out app lifecycle

  public IngestionController (sensorDataService sensorDataService) {
    this.sensorDataService = sensorDataService;
  }

  // @PostMapping("/api/sensor") // endpoint to receive sensor data
  // the incoming sensor data is contained in its respective dto
  // http json -> java object
  // public ResponseEntity<String> receiveSensorData(@RequestBody sensorData data) {
    // now 'data' contains the incoming json data'
    // nice to print to console for logs

    // try {
    // sensorDataService.validateSensorData(data); 
    // return ResponseEntity.ok("Data received successfully! ");
  
    // } catch (IllegalArgumentException e) {
    //   return ResponseEntity
    //   .status(HttpStatus.BAD_REQUEST)
    //   .body("Error when validating incoming sensor data: " + e.getMessage()); //400 error
    // }
    
    
  //}

}