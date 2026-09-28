package com.example.hydra.controller;

import com.example.hydra.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.example.hydra.dto.UserData;
import com.example.hydra.entity.UserEntity;
import com.example.hydra.dto.LoginRequest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
/**
 * Controls user operations
 * UserController
 */
public class UserController {

  private final UserService userService;
  
  public UserController (UserService userService) {
    this.userService = userService;
  }
  @PostMapping("/api/createAccount") 
  public ResponseEntity<String> createAccount(@RequestBody UserData userData ) {
    // jackson automatically deserialized json and saves it to DTO
    userService.saveUser(userData);
    return ResponseEntity.ok("Sucessfully created your account, " + userData.getFirst_name());   
  }
  /**
   * 201 success -> UID, HID, email
   * 400 bad request - > email already exists, username taken, invalid password
   */
  @PostMapping("api/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
    UserEntity authenticatedUser = userService.authenticate(loginRequest);
    // create session
    session.setAttribute("userId", authenticatedUser.getUserId());
    return ResponseEntity.ok("Sucessfully logged in"); 
  }

  @GetMapping ("api/me")
  public ResponseEntity<String> me(HttpSession session) {
    UUID userId = (UUID) session.getAttribute("userId");
     if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("You are not logged in");
    }
    return ResponseEntity.ok("Logged-in user: " + userId);
  }

  // logout endpoint
  // user/me -> current user's data

  /**
   * 201 success -> HID, name, household created
   * 400 bad request - > email already exists
   */
  // @PostMapping("api/join_household")
  /**
   * 200 OK -> joined household message
   * 400 bad request - > invlaid code, retry
   */




  // @PostMapping("api/generate_join_code")
  // @PostMapping("api/login")
  // @PostMapping("api/add_plant") or to plant controller? 
  // @PostMapping("api/remove_plant")
  // @PostMapping("api/edit_plant_details") 
}
