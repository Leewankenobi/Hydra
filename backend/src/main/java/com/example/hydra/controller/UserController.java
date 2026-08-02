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
  // @PostMapping("api/signup")
  // @PostMapping("api/login")
  // @PostMapping("api/create_household")
  // @PostMapping("api/join_household")
  // @PostMapping("api/add_plant") or to plant controller? 
  // @PostMapping("api/remove_plant")
  // @PostMapping("api/edit_plant_details")
  // @PostMapping("api/generate_join_code")


  
}
