package com.example.hydra.controller;

import com.example.hydra.service.UserService;
import org.springframework.web.bind.annotation.RestController;


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
  // @PostMapping("api/createAccount")  // household name is also provided
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
