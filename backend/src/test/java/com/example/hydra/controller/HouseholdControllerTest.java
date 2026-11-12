package com.example.hydra.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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

import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.UserEntity;
import com.example.hydra.service.HouseholdService;
import com.example.hydra.service.UserService;

@WebMvcTest({HouseholdController.class, UserController.class})
@ActiveProfiles("test")
class HouseholdControllerTest {

  private static final String ACCOUNT_PAYLOAD = """
      {
        "firstName": "New",
        "lastName": "Name",
        "username": "new-user"
      }
      """;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private HouseholdService householdService;

  @MockitoBean
  private UserService userService;

  @Test
  void getHouseholdWithoutSessionReturnsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/households"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getHouseholdWithSessionReturnsMembers() throws Exception {
    UUID userId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    household.setHouseholdName("Home");
    household.setinviteCode(123456);

    UserEntity current = user(userId, "Liya", "Z", "liya", "liya@example.com");
    UserEntity other = user(UUID.randomUUID(), "Other", "Person", "other", "other@example.com");

    when(userService.getHouseholdForUser(userId)).thenReturn(household);
    when(userService.getHouseholdMembers(userId)).thenReturn(List.of(current, other));

    mockMvc.perform(get("/api/households").session(sessionWith(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.householdName").value("Home"))
        .andExpect(jsonPath("$.inviteCode").value(123456))
        .andExpect(jsonPath("$.currentUserId").value(userId.toString()))
        .andExpect(jsonPath("$.members.length()").value(2))
        .andExpect(jsonPath("$.members[0].currentUser").value(true))
        .andExpect(jsonPath("$.members[1].currentUser").value(false));
  }

  @Test
  void updateMeWithoutSessionReturnsUnauthorized() throws Exception {
    mockMvc.perform(put("/api/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ACCOUNT_PAYLOAD))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void updateMeWithSessionReturnsOk() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(put("/api/me")
            .session(sessionWith(userId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(ACCOUNT_PAYLOAD))
        .andExpect(status().isOk());

    verify(userService).updateAccount(userId, "New", "Name", "new-user");
  }

  @Test
  void deleteMeWithoutSessionReturnsUnauthorized() throws Exception {
    mockMvc.perform(delete("/api/me"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void deleteMeWithSessionReturnsOk() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(delete("/api/me").session(sessionWith(userId)))
        .andExpect(status().isOk());

    verify(userService).deleteAccount(userId);
  }

  @Test
  void deleteMeRejectsLastMember() throws Exception {
    UUID userId = UUID.randomUUID();

    doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete the last household member"))
        .when(userService).deleteAccount(userId);

    mockMvc.perform(delete("/api/me").session(sessionWith(userId)))
        .andExpect(status().isConflict());
  }

  private MockHttpSession sessionWith(UUID userId) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("userId", userId);
    return session;
  }

  private UserEntity user(
      UUID userId,
      String firstName,
      String lastName,
      String username,
      String email) {

    UserEntity user = new UserEntity();
    user.setUserId(userId);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(username);
    user.setEmail(email);
    return user;
  }
}
