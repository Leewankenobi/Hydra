package com.example.hydra.service;

import org.springframework.stereotype.Service;
import com.example.hydra.repository.UserRepository;

@Service
public class UserService {

  public final UserRepository userRepository;
  public UserService(UserRepository userRepository){
    this.userRepository = userRepository; 
  }

  
}
