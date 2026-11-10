package com.example.hydra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hydra.dto.SensorData;
import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.PlantEntity;
import com.example.hydra.entity.ReadingEntity;
import com.example.hydra.entity.UserEntity;
import com.example.hydra.repository.PlantRepository;
import com.example.hydra.repository.ReadingsRepository;
import com.example.hydra.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SensorDataServiceTest {

  @Mock
  private ReadingsRepository readingRepository;

  @Mock
  private PlantRepository plantRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private SensorDataService sensorDataService;

  @Test
  void validateRejectsMissingDeviceId() {
    SensorData data = validData();
    data.setDeviceId(null);

    assertThatThrownBy(() -> sensorDataService.validateSensorData(data))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Device ID");
  }

  @Test
  void validateRejectsBlankDeviceId() {
    SensorData data = validData();
    data.setDeviceId("   ");

    assertThatThrownBy(() -> sensorDataService.validateSensorData(data))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Device ID");
  }

  @Test
  void validateRejectsMissingMoisture() {
    SensorData data = validData();
    data.setMoistureLevel(null);

    assertThatThrownBy(() -> sensorDataService.validateSensorData(data))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Moisture level");
  }

  @Test
  void validateRejectsMoistureBelowRange() {
    SensorData data = validData();
    data.setMoistureLevel(199);

    assertThatThrownBy(() -> sensorDataService.validateSensorData(data))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("200 to 2000");
  }

  @Test
  void validateRejectsMoistureAboveRange() {
    SensorData data = validData();
    data.setMoistureLevel(2001);

    assertThatThrownBy(() -> sensorDataService.validateSensorData(data))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("200 to 2000");
  }

  @Test
  void validateAcceptsBoundaryValues() {
    SensorData lower = validData();
    lower.setMoistureLevel(200);

    SensorData upper = validData();
    upper.setMoistureLevel(2000);

    assertThatCode(() -> sensorDataService.validateSensorData(lower))
        .doesNotThrowAnyException();
    assertThatCode(() -> sensorDataService.validateSensorData(upper))
        .doesNotThrowAnyException();
  }

  @Test
  void saveThrowsWhenDeviceIsNotPaired() {
    SensorData data = validData();
    data.setDeviceId("unknown-device");

    when(plantRepository.findByDeviceId("unknown-device")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> sensorDataService.saveSensorData(data))
        .isInstanceOf(SensorDataService.DeviceNotPairedException.class)
        .hasMessageContaining("unknown-device");

    verifyNoInteractions(readingRepository);
  }

  @Test
  void saveResolvesPlantAndCopiesPayloadToReading() {
    SensorData data = validData();
    data.setDeviceId(" PICO-01 ");
    data.setMoistureLevel(900);
    data.setTemperature(21.5);

    PlantEntity plant = new PlantEntity();
    plant.setPlantId(UUID.randomUUID());
    plant.setDeviceId("pico-01");
    plant.setMinMoistureLevel(30);

    when(plantRepository.findByDeviceId("pico-01")).thenReturn(Optional.of(plant));
    when(readingRepository.findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId()))
        .thenReturn(Optional.empty());
    when(readingRepository.save(any(ReadingEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    sensorDataService.saveSensorData(data);

    ArgumentCaptor<ReadingEntity> captor = ArgumentCaptor.forClass(ReadingEntity.class);
    verify(readingRepository).save(captor.capture());

    ReadingEntity reading = captor.getValue();
    assertThat(reading.getPlantEntity()).isSameAs(plant);
    assertThat(reading.getDeviceId()).isEqualTo("pico-01");
    assertThat(reading.getMoisture_reading()).isEqualTo(900);
    assertThat(reading.getTemperature()).isEqualTo(21.5);
    assertThat(reading.getRecordedAt()).isNotNull();
  }

  @Test
  void saveSendsEmailWhenCrossingBelowMinimum() {
    SensorData data = validData();
    data.setMoistureLevel(300);

    PlantEntity plant = plantWithMinimum(30);
    HouseholdEntity household = plant.getHousehold();
    ReadingEntity previous = new ReadingEntity();
    previous.setMoisture_reading(900);

    UserEntity member = new UserEntity();
    member.setEmail("member@example.com");

    when(plantRepository.findByDeviceId("pico-01")).thenReturn(Optional.of(plant));
    when(readingRepository.findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId()))
        .thenReturn(Optional.of(previous));
    when(userRepository.findByHousehold(household)).thenReturn(List.of(member));
    when(readingRepository.save(any(ReadingEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    sensorDataService.saveSensorData(data);

    verify(notificationService).sendLowMoistureEmail(
        eq(List.of("member@example.com")),
        eq("Fern"),
        eq(6),
        eq(30),
        any()
    );
  }

  @Test
  void saveSendsEmailWhenFirstReadingIsLow() {
    SensorData data = validData();
    data.setMoistureLevel(300);

    PlantEntity plant = plantWithMinimum(30);
    HouseholdEntity household = plant.getHousehold();

    UserEntity member = new UserEntity();
    member.setEmail("member@example.com");

    when(plantRepository.findByDeviceId("pico-01")).thenReturn(Optional.of(plant));
    when(readingRepository.findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId()))
        .thenReturn(Optional.empty());
    when(userRepository.findByHousehold(household)).thenReturn(List.of(member));
    when(readingRepository.save(any(ReadingEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    sensorDataService.saveSensorData(data);

    verify(notificationService).sendLowMoistureEmail(
        eq(List.of("member@example.com")),
        eq("Fern"),
        eq(6),
        eq(30),
        any()
    );
  }

  @Test
  void saveDoesNotSendEmailWhenPreviousReadingWasAlreadyLow() {
    SensorData data = validData();
    data.setMoistureLevel(300);

    PlantEntity plant = plantWithMinimum(30);
    ReadingEntity previous = new ReadingEntity();
    previous.setMoisture_reading(300);

    when(plantRepository.findByDeviceId("pico-01")).thenReturn(Optional.of(plant));
    when(readingRepository.findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId()))
        .thenReturn(Optional.of(previous));
    when(readingRepository.save(any(ReadingEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    sensorDataService.saveSensorData(data);

    verifyNoInteractions(notificationService);
    verifyNoInteractions(userRepository);
  }

  @Test
  void saveDoesNotSendEmailWhenReadingIsAboveMinimum() {
    SensorData data = validData();
    data.setMoistureLevel(900);

    PlantEntity plant = plantWithMinimum(30);
    ReadingEntity previous = new ReadingEntity();
    previous.setMoisture_reading(300);

    when(plantRepository.findByDeviceId("pico-01")).thenReturn(Optional.of(plant));
    when(readingRepository.findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId()))
        .thenReturn(Optional.of(previous));
    when(readingRepository.save(any(ReadingEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    sensorDataService.saveSensorData(data);

    verifyNoInteractions(notificationService);
    verifyNoInteractions(userRepository);
  }

  @Test
  void notificationFailureDoesNotFailSave() {
    SensorData data = validData();
    data.setMoistureLevel(300);

    PlantEntity plant = plantWithMinimum(30);
    HouseholdEntity household = plant.getHousehold();

    UserEntity member = new UserEntity();
    member.setEmail("member@example.com");

    when(plantRepository.findByDeviceId("pico-01")).thenReturn(Optional.of(plant));
    when(readingRepository.findTopByPlantEntity_PlantIdOrderByRecordedAtDesc(plant.getPlantId()))
        .thenReturn(Optional.empty());
    when(userRepository.findByHousehold(household)).thenReturn(List.of(member));
    when(readingRepository.save(any(ReadingEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    doThrow(new RuntimeException("mail queue down"))
        .when(notificationService)
        .sendLowMoistureEmail(anyList(), anyString(), anyInt(), anyInt(), any());

    assertThatCode(() -> sensorDataService.saveSensorData(data))
        .doesNotThrowAnyException();

    verify(readingRepository).save(any(ReadingEntity.class));
  }

  private PlantEntity plantWithMinimum(int minimum) {
    PlantEntity plant = new PlantEntity();
    plant.setPlantId(UUID.randomUUID());
    plant.setDeviceId("pico-01");
    plant.setNickname("Fern");
    plant.setMinMoistureLevel(minimum);
    plant.setHousehold(new HouseholdEntity());
    return plant;
  }

  private SensorData validData() {
    SensorData data = new SensorData();
    data.setDeviceId("pico-01");
    data.setMoistureLevel(900);
    data.setTemperature(21.5);
    return data;
  }
}
