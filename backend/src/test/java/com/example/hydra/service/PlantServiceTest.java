package com.example.hydra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

  @Test
  void updatePlantUpdatesEditableFieldsAndNormalizesDeviceId() {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    PlantEntity plant = plantWithId(plantId, "pico-01", "old-key");

    PlantRequest request = updateRequest(" PICO-02 ", 20, 80);
    request.setNickname("New Fern");
    request.setPlantType("OUTDOOR");

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByPlantIdAndHousehold(plantId, household))
        .thenReturn(Optional.of(plant));
    when(plantRepository.findByDeviceId("pico-02")).thenReturn(Optional.empty());

    plantService.updatePlant(plantId, request, userId);

    assertThat(plant.getNickname()).isEqualTo("New Fern");
    assertThat(plant.getSpecies()).isEqualTo("Boston fern");
    assertThat(plant.getPlantType()).isEqualTo("OUTDOOR");
    assertThat(plant.getMinMoistureLevel()).isEqualTo(20);
    assertThat(plant.getMaxMoistureLevel()).isEqualTo(80);
    assertThat(plant.getDeviceId()).isEqualTo("pico-02");
    verify(plantRepository).saveAndFlush(plant);
  }

  @Test
  void updatePlantRejectsDeviceIdUsedByAnotherPlant() {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    PlantEntity plant = plantWithId(plantId, "pico-01", null);
    PlantEntity other = plantWithId(UUID.randomUUID(), "pico-02", null);

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByPlantIdAndHousehold(plantId, household))
        .thenReturn(Optional.of(plant));
    when(plantRepository.findByDeviceId("pico-02")).thenReturn(Optional.of(other));

    assertThatThrownBy(() -> plantService.updatePlant(
        plantId, updateRequest("pico-02", 20, 80), userId))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

    verify(plantRepository, never()).saveAndFlush(any(PlantEntity.class));
  }

  @Test
  void updatePlantRejectsInvalidThresholds() {
    assertThatThrownBy(() -> plantService.updatePlant(
        UUID.randomUUID(), updateRequest("pico-01", 80, 70), UUID.randomUUID()))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

    verifyNoInteractions(plantRepository);
    verifyNoInteractions(userService);
  }

  @Test
  void updatePlantRejectsPlantOutsideHousehold() {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByPlantIdAndHousehold(plantId, household))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> plantService.updatePlant(
        plantId, updateRequest("pico-01", 20, 80), userId))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

    verify(plantRepository, never()).saveAndFlush(any(PlantEntity.class));
  }

  @Test
  void deletePlantDeletesImageAndRelatedRows() {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    PlantEntity plant = plantWithId(plantId, "pico-01", "image-key");

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByPlantIdAndHousehold(plantId, household))
        .thenReturn(Optional.of(plant));

    plantService.deletePlant(plantId, userId);

    verify(imageService).deleteImage("image-key");
    verify(readingsRepository).deleteByPlantId(plantId);
    verify(plantRepository).delete(plant);
    verify(plantRepository).flush();
  }

  @Test
  void deletePlantAbortsWhenImageDeletionFails() {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    PlantEntity plant = plantWithId(plantId, "pico-01", "image-key");

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByPlantIdAndHousehold(plantId, household))
        .thenReturn(Optional.of(plant));
    doThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Filebase down"))
        .when(imageService).deleteImage("image-key");

    assertThatThrownBy(() -> plantService.deletePlant(plantId, userId))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY));

    verifyNoInteractions(readingsRepository);
    verify(plantRepository, never()).delete(any(PlantEntity.class));
    verify(plantRepository, never()).flush();
  }

  @Test
  void deletePlantWithoutImageDeletesRows() {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    PlantEntity plant = plantWithId(plantId, "pico-01", null);

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByPlantIdAndHousehold(plantId, household))
        .thenReturn(Optional.of(plant));

    plantService.deletePlant(plantId, userId);

    verifyNoInteractions(imageService);
    verify(readingsRepository).deleteByPlantId(plantId);
    verify(plantRepository).delete(plant);
    verify(plantRepository).flush();
  }

  @Test
  void deletePlantRejectsPlantOutsideHousehold() {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(plantRepository.findByPlantIdAndHousehold(plantId, household))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> plantService.deletePlant(plantId, userId))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));

    verifyNoInteractions(imageService);
    verifyNoInteractions(readingsRepository);
    verify(plantRepository, never()).delete(any(PlantEntity.class));
  }

  private PlantRequest updateRequest(String deviceId, int min, int max) {
    PlantRequest request = new PlantRequest();
    request.setNickname("Fern");
    request.setSpecies("Boston fern");
    request.setPlantType("INDOOR");
    request.setMinMoistureLevel(min);
    request.setMaxMoistureLevel(max);
    request.setDeviceId(deviceId);
    return request;
  }

  private PlantEntity plantWithId(UUID plantId, String deviceId, String imageKey) {
    PlantEntity plant = new PlantEntity();
    plant.setPlantId(plantId);
    plant.setDeviceId(deviceId);
    plant.setImageKey(imageKey);
    return plant;
  }
}
