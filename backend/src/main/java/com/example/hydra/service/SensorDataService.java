package com.example.hydra.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.hydra.dto.SensorData;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.entity.ReadingEntity;
import com.example.hydra.repository.ReadingsRepository;
import com.example.hydra.repository.PlantRepository;

import java.time.OffsetDateTime;

@Service
public class SensorDataService {
  private final ReadingsRepository readingRepository;
  private final PlantRepository plantRepository;
  
  // inject both repositories
  public SensorDataService(ReadingsRepository readingsRepository, PlantRepository plantRepository){
    this.readingRepository = readingsRepository;
    this.plantRepository = plantRepository;
  }
  public void validateSensorData(SensorData data) {
    // wrong data types are handled by spring 
    if (data.getPlantId() == null) {
      throw new IllegalArgumentException("Plant ID is required! ");
    }
    if (data.getMoistureLevel() == null) {
      throw new IllegalArgumentException("Mositure level is required! "); 
    }
    if(data.getMoistureLevel() < 200 || data.getMoistureLevel() > 2000) {
      throw new IllegalArgumentException("Mositure level should be within 200 to 2000 range"); 
    }
  }
  public void saveSensorData(SensorData data) {
    // look up plant by id
    // returns 'optional' containter
    Optional<PlantEntity> p = plantRepository.findById(data.getPlantId()); 
    if (p.isPresent()) { // plant found
      PlantEntity plantEntity = p.get();
      // create a readingEntity + attach timezone then save to repo
      ReadingEntity reading = new ReadingEntity(); 
      reading.setPlantEntity(plantEntity);
      reading.setMoisture_reading(data.getMoistureLevel().intValue());
      reading.setRecordedAt(OffsetDateTime.now());
      System.out.println("Reading: " + reading.toString());
      readingRepository.save(reading); // save to db
    }
    else {
      throw new IllegalArgumentException("Plant not registered, register plant? ");
    }
  }  
}
