package com.example.hydra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.hydra.dto.PlantData;
import com.example.hydra.dto.PlantRequest;
import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.entity.ReadingEntity;
import com.example.hydra.repository.PlantRepository;
import com.example.hydra.repository.ReadingsRepository;

@ExtendWith(MockitoExtension.class)
class PlantServiceTest {

  @Mock
  private PlantRepository plantRepository;

  @Mock
  private ReadingsRepository readingsRepository;

  @Mock
  private UserService userService;

  @Mock
  private ImageService imageService;

  @InjectMocks
  private PlantService plantService;

  @Test
  void rejectsDuplicateDeviceIdBeforeUploadingImage() {
    PlantRequest request = new PlantRequest();
    request.setNickname("Fern");
    request.setSpecies("Boston fern");
    request.setPlantType("INDOOR");
    request.setMinMoistureLevel(30);
    request.setMaxMoistureLevel(70);
    request.setDeviceId(" PICO-01 ");

    MockMultipartFile image = new MockMultipartFile(
        "plantImage",
        "fern.png",
        "image/png",
        new byte[] { 1, 2, 3 }
    );

    when(plantRepository.existsByDeviceId("pico-01")).thenReturn(true);

    assertThatThrownBy(() -> plantService.savePlant(request, image, UUID.randomUUID()))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

    verifyNoInteractions(imageService);
    verifyNoInteractions(userService);
  }

  @Test
  void getAllPlantsIncludesLatestReading() {
    UUID userId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();

    PlantEntity plant = new PlantEntity();
    plant.setPlantId(UUID.randomUUID());
    plant.setNickname("Fern");
    plant.setMinMoistureLevel(30);
    plant.setMaxMoistureLevel(70);

    ReadingEntity reading = new ReadingEntity();
    reading.setMoisture_reading(900);
    reading.setRecordedAt(OffsetDateTime.now());

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByHousehold(household)).thenReturn(List.of(plant));
    when(readingsRepository.findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId()))
        .thenReturn(Optional.of(reading));

    List<PlantData> plants = plantService.getAllPlants(userId);

    assertThat(plants).hasSize(1);
    assertThat(plants.get(0).getLatestMoisture()).isEqualTo(900);
    assertThat(plants.get(0).getLatestMoisturePercent()).isEqualTo(39);
    assertThat(plants.get(0).getLatestRecordedAt()).isEqualTo(reading.getRecordedAt());
  }
}
