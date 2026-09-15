package com.example.hydra.controller;

import com.example.hydra.service.UserService;
import com.example.hydra.dto.UserData;
import com.example.hydra.dto.LoginRequest;

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
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {}



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
