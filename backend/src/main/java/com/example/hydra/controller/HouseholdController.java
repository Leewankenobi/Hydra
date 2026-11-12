package com.example.hydra.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.UserEntity;
import com.example.hydra.service.HouseholdService;
import com.example.hydra.service.UserService;

@RestController 
public class HouseholdController {

  private final HouseholdService householdService;
  private final UserService userService;

  public HouseholdController(
      HouseholdService householdService,
      UserService userService) {
    this.householdService = householdService;
    this.userService = userService;
  }

  @PostMapping ("api/households/validateCode")
  public ResponseEntity<String> validateCode(@RequestBody Integer joinCode){
      return householdService.validateCode(joinCode);
  }

  @GetMapping("api/households")
  public ResponseEntity<?> getHousehold(HttpSession session) {
    UUID userId = (UUID) session.getAttribute("userId");
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("You are not logged in");
    }

    HouseholdEntity household = userService.getHouseholdForUser(userId);
    List<MemberData> members = userService.getHouseholdMembers(userId)
        .stream()
        .map(member -> toMemberData(member, userId))
        .toList();

    return ResponseEntity.ok(new HouseholdData(
        household.getHouseholdName(),
        household.getinviteCode(),
        userId,
        members
    ));
  }

  private MemberData toMemberData(UserEntity member, UUID currentUserId) {
    return new MemberData(
        member.getUserId(),
        member.getFirstName(),
        member.getLastName(),
        member.getUsername(),
        member.getEmail(),
        member.getUserId().equals(currentUserId)
    );
  }

  public record MemberData(
      UUID userId,
      String firstName,
      String lastName,
      String username,
      String email,
      boolean currentUser) {}

  public record HouseholdData(
      String householdName,
      Integer inviteCode,
      UUID currentUserId,
      List<MemberData> members) {}
}
