package com.example.hydra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.UserEntity;
import com.example.hydra.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private HouseholdService householdService;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @Test
  void getHouseholdMembersReturnsHouseholdUsers() {
    UUID userId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    UserEntity user = user(userId, household, "Liya", "Z", "liya", "liya@example.com");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.findByHousehold(household)).thenReturn(List.of(user));

    assertThat(userService.getHouseholdMembers(userId)).containsExactly(user);
  }

  @Test
  void updateAccountUpdatesNamesAndUsernameButNotEmailOrPassword() {
    UUID userId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    UserEntity user = user(userId, household, "Old", "Name", "old", "liya@example.com");
    user.setPasswordHash("hashed-password");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    userService.updateAccount(userId, " New ", " Person ", " new-user ");

    assertThat(user.getFirstName()).isEqualTo("New");
    assertThat(user.getLastName()).isEqualTo("Person");
    assertThat(user.getUsername()).isEqualTo("new-user");
    assertThat(user.getEmail()).isEqualTo("liya@example.com");
    assertThat(user.getPasswordHash()).isEqualTo("hashed-password");
    verify(userRepository).saveAndFlush(user);
  }

  @Test
  void updateAccountRejectsBlankNames() {
    UUID userId = UUID.randomUUID();
    UserEntity user = user(userId, new HouseholdEntity(), "Liya", "Z", "liya", "liya@example.com");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> userService.updateAccount(userId, " ", "Z", "liya"))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

    verify(userRepository, never()).saveAndFlush(user);
  }

  @Test
  void deleteAccountDeletesWhenOtherMembersExist() {
    UUID userId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    UserEntity user = user(userId, household, "Liya", "Z", "liya", "liya@example.com");
    UserEntity other = user(UUID.randomUUID(), household, "Other", "Person", "other", "other@example.com");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.findByHousehold(household)).thenReturn(List.of(user, other));

    userService.deleteAccount(userId);

    verify(userRepository).delete(user);
    verify(userRepository).flush();
  }

  @Test
  void deleteAccountRejectsLastMember() {
    UUID userId = UUID.randomUUID();
    HouseholdEntity household = new HouseholdEntity();
    UserEntity user = user(userId, household, "Liya", "Z", "liya", "liya@example.com");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.findByHousehold(household)).thenReturn(List.of(user));

    assertThatThrownBy(() -> userService.deleteAccount(userId))
        .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

    verify(userRepository, never()).delete(user);
  }

  private UserEntity user(
      UUID userId,
      HouseholdEntity household,
      String firstName,
      String lastName,
      String username,
      String email) {

    UserEntity user = new UserEntity();
    user.setUserId(userId);
    user.setHousehold(household);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(username);
    user.setEmail(email);
    return user;
  }
}
