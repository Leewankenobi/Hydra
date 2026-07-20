package com.example.hydra.repository;
import com.example.hydra.entity.UserEntity; 
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// JPA interface that provides CRUD operations for userEntity
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
