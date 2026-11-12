package com.example.hydra.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.hydra.dto.PlantRequest;
import com.example.hydra.service.PlantService;

@WebMvcTest(PlantController.class)
@ActiveProfiles("test")
class PlantControllerTest {

  private static final String UPDATE_PAYLOAD = """
      {
        "nickname": "Fern",
        "species": "Boston fern",
        "plantType": "INDOOR",
        "minMoistureLevel": 30,
        "maxMoistureLevel": 70,
        "deviceId": "pico-01"
      }
      """;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PlantService plantService;

  @Test
  void updateWithoutSessionReturnsUnauthorized() throws Exception {
    mockMvc.perform(put("/api/plants/{plantId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(UPDATE_PAYLOAD))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void updateWithSessionReturnsOk() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();

    mockMvc.perform(put("/api/plants/{plantId}", plantId)
            .session(sessionWith(userId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(UPDATE_PAYLOAD))
        .andExpect(status().isOk());

    verify(plantService).updatePlant(eq(plantId), any(PlantRequest.class), eq(userId));
  }

  @Test
  void updateReturnsNotFoundWhenServiceRejectsPlant() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"))
        .when(plantService).updatePlant(eq(plantId), any(PlantRequest.class), eq(userId));

    mockMvc.perform(put("/api/plants/{plantId}", plantId)
            .session(sessionWith(userId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(UPDATE_PAYLOAD))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteWithoutSessionReturnsUnauthorized() throws Exception {
    mockMvc.perform(delete("/api/plants/{plantId}", UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void deleteWithSessionReturnsOk() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();

    mockMvc.perform(delete("/api/plants/{plantId}", plantId)
            .session(sessionWith(userId)))
        .andExpect(status().isOk());

    verify(plantService).deletePlant(plantId, userId);
  }

  @Test
  void deleteReturnsBadGatewayWhenImageDeletionFails() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID plantId = UUID.randomUUID();

    doThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Filebase down"))
        .when(plantService).deletePlant(plantId, userId);

    mockMvc.perform(delete("/api/plants/{plantId}", plantId)
            .session(sessionWith(userId)))
        .andExpect(status().isBadGateway());
  }

  private MockHttpSession sessionWith(UUID userId) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("userId", userId);
    return session;
  }
}
