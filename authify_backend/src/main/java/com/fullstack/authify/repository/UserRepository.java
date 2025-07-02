package com.fullstack.authify.repository;

import com.fullstack.authify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    // check if an email already exists
    Boolean existsByEmail(String email);


}
