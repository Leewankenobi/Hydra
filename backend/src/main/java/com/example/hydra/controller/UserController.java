package com.example.hydra.controller;

import com.example.hydra.service.UserService;
import com.example.hydra.dto.UserData;
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
  @PostMapping("api/createAccount") 
  public void createAccount(@RequestParam String household_name,
    @RequestParam String first_name,
    @RequestParam String last_name, 
    @RequestParam String email,
    @RequestParam String username, 
    @RequestParam String password_hash ) {

    UserData userData = new UserData();
    userData.setHousehold_name(household_name);
    userData.setFirst_name(first_name);
    userData.setLast_name(last_name);
    userData.setEmail(email);
    userData.setUsername(username);
    userData.setPasswordhash(password_hash);
    
    userService.saveUser(userData);
  }
  
  
  // household name is also provided
  // deserialize and send to service

  /**
   * 201 success -> UID, HID, email
   * 400 bad request - > email already exists, username taken, invalid password
   */

  /**
   * @PostMapping("api/login")
   */
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
