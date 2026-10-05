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
import java.util.UUID;

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
    this.passwordEncoder = passwordEncoder; 
  }

  public UserEntity saveUser(UserData userData) {
    String email = userData.getEmail().trim().toLowerCase(Locale.ROOT);
    Integer joinCode = userData.getJoinCode();
    String householdName = userData.getHousehold_name();
    boolean hasJoinCode = joinCode != null;
    boolean hasHouseholdName = householdName != null && !householdName.isBlank();

    // reject if email already exists
    if (userRepository.findByEmail(email).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    // reject if neither join code nor household is provided
    if(!hasJoinCode && !hasHouseholdName){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either join code ot household name must be provided.");
    }
    // reject if both is provided
    if(hasJoinCode && hasHouseholdName){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both join code and hosuehold name can not be provided");
    }

    UserEntity user = new UserEntity();


    // join household -> join code only, not hosuehold name
    if(hasJoinCode && !hasHouseholdName){
      HouseholdEntity existingHousehold = householdService.getHouseholdByCode(joinCode); 
      user.setHousehold(existingHousehold);
    }
    // create household -> household name only
    if(hasHouseholdName && !hasJoinCode) {
      // create houehold entity and assign to user
      HouseholdEntity household = householdService.createHousehold(userData.getHousehold_name().trim()); 
      user.setHousehold(household);
    }     
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

  public HouseholdEntity getHouseholdForUser(UUID userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"User with such ID not found")
        );
    return user.getHousehold();
  }
}
