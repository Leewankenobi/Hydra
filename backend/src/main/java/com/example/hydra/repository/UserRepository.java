package com.example.hydra.repository;
import com.example.hydra.entity.UserEntity; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;


@Repository
// JPA interface that provides CRUD operations for userEntity
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  // get User by email
  Optional<UserEntity> findByEmail(String email);

}


