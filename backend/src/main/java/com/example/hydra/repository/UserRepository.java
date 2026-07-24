package com.example.hydra.repository;
import com.example.hydra.entity.UserEntity; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
// JPA interface that provides CRUD operations for userEntity
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
