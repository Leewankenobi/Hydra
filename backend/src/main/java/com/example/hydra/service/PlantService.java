package com.example.hydra.service;

import org.springframework.stereotype.Service;

import com.example.hydra.repository.PlantRepository;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.dto.PlantRequest;

@Service 
public class PlantService {

  private final PlantRepository plantRepository;
  
  public PlantService(PlantRepository plantRepository) {this.plantRepository = plantRepository;}
  
  public PlantEntity savePlant(PlantRequest plantRequest){}


  
}
