package com.example.hydra.repository;
import com.example.hydra.entity.UserEntity; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import com.example.hydra.entity.HouseholdEntity;


@Repository
// JPA interface that provides CRUD operations for userEntity
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  // get User by email
  Optional<UserEntity> findByEmail(String email);
  Optional<UserEntity> findById(UUID Id);

  List<UserEntity> findByHousehold(HouseholdEntity household);

}


