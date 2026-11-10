package com.example.hydra.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hydra.service.SensorDataService;
import com.example.hydra.service.SensorDataService.DeviceNotPairedException;

@WebMvcTest(IngestionController.class)
@ActiveProfiles("test")
class IngestionControllerTest {

  private static final String VALID_PAYLOAD =
      "{\"device_id\":\"pico-01\",\"moisture\":900,\"temperature\":21.5}";

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SensorDataService sensorDataService;

  @Test
  void returnsOkWhenReadingIsSaved() throws Exception {
    mockMvc.perform(post("/api/sensor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_PAYLOAD))
        .andExpect(status().isOk())
        .andExpect(content().string("Saved Reading!"));
  }

  @Test
  void returnsBadRequestWhenValidationFails() throws Exception {
    doThrow(new IllegalArgumentException("Device ID is required"))
        .when(sensorDataService).validateSensorData(any());

    mockMvc.perform(post("/api/sensor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_PAYLOAD))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Device ID is required"));
  }

  @Test
  void returnsNotFoundWhenDeviceIsNotPaired() throws Exception {
    doNothing().when(sensorDataService).validateSensorData(any());
    doThrow(new DeviceNotPairedException("No plant is paired with device pico-01"))
        .when(sensorDataService).saveSensorData(any());

    mockMvc.perform(post("/api/sensor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_PAYLOAD))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No plant is paired with device pico-01"));
  }

  @Test
  void returnsInternalServerErrorOnDatabaseFailure() throws Exception {
    doNothing().when(sensorDataService).validateSensorData(any());
    doThrow(new DataAccessResourceFailureException("database unavailable"))
        .when(sensorDataService).saveSensorData(any());

    mockMvc.perform(post("/api/sensor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_PAYLOAD))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Could not save reading"));
  }
}
