package com.example.hydra.service;
import com.example.hydra.dto.UserData;
import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.UserEntity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.hydra.repository.UserRepository;
import com.example.hydra.service.HouseholdService;


@Service
public class UserService {

  public final UserRepository userRepository;
  public final HouseholdService householdService;

  public UserService(UserRepository userRepository, HouseholdService householdService){
    this.userRepository = userRepository; 
    this.householdService = householdService;
  }

  public UserEntity saveUser(UserData userData) {

    // check if email already exists - send response to frontend
    if (userRepository.findByEmail(userData.getEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
  
    // create houehold entity and save to db
    HouseholdEntity household = householdService.createHousehold(userData.getHousehold_name()); 

    // create user entity - fields from user data
    // assign created houehold to user

    UserEntity user = new UserEntity();
    user.setHousehold(household);
    user.setFirstName(userData.getFirst_name());
    user.setLastName(userData.getLast_name());
    user.setEmail(userData.getEmail());
    user.setUsername(userData.getUsername());
    user.setPasswordHash(userData.getPasswordhash());

    // save and return the userEntity
    UserEntity savedUser = userRepository.save(user);
    return savedUser;
  
  }  
}
