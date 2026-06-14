package com.fitness.userservice.repository;

import com.fitness.userservice.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisterRepository extends JpaRepository<User, String> {

    User findByEmail(String email);

    Boolean existsByKeyCloakId(String userId);

    Boolean existsByEmail(String email);
}
