package com.example.hydra.service;
import com.example.hydra.dto.LoginRequest;
import com.example.hydra.dto.UserData;
import com.example.hydra.entity.HouseholdEntity;
import com.example.hydra.entity.UserEntity;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Locale;
import java.util.Optional;

import com.example.hydra.repository.UserRepository;
import com.example.hydra.service.HouseholdService;


@Service
public class UserService {

  public final UserRepository userRepository;
  public final HouseholdService householdService;
  public final BCryptPasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, HouseholdService householdService, BCryptPasswordEncoder passwordEncoder){
    this.userRepository = userRepository; 
    this.householdService = householdService;
    this. passwordEncoder = passwordEncoder; 
  }

  public UserEntity saveUser(UserData userData) {
    String email = userData.getEmail().trim().toLowerCase(Locale.ROOT);

    // check if email already exists - send response to frontend
    if (userRepository.findByEmail(email).isPresent()) {
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
    user.setEmail(email);
    user.setUsername(userData.getUsername());
    user.setPasswordHash(passwordEncoder.encode(userData.getPasswordHash())); // hash ya password!

    // save and return the userEntity
    try {
      return userRepository.saveAndFlush(user);
    } catch (DataIntegrityViolationException exception) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists", exception);
    }
  
  } 

  public UserEntity authenticate(LoginRequest loginRequest) {

    String email = loginRequest.getEmail().trim().toLowerCase(Locale.ROOT);
    String rawPassword =  loginRequest.getPassword();

    Optional<UserEntity> user = userRepository.findByEmail(email);   
    if (user.isEmpty()) { // reject if no user with that email is found
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    } 
    UserEntity existingUser = user.get(); 

    if(!passwordEncoder.matches(rawPassword, existingUser.getPasswordHash())){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }  
    return existingUser;    

  }

}
