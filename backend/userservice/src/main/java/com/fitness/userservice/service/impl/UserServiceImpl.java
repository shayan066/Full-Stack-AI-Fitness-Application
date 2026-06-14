package com.fitness.userservice.service.impl;

import com.fitness.userservice.model.entity.User;
import com.fitness.userservice.model.enums.UserRole;
import com.fitness.userservice.model.request.RegisterRequest;
import com.fitness.userservice.model.response.UserResponse;
import com.fitness.userservice.repository.RegisterRepository;
import com.fitness.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final RegisterRepository registerRepository;

    public UserServiceImpl(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    @Override
    public UserResponse getUserProfile(String userId) {
        User user = registerRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    @Override
    public UserResponse registerUser(RegisterRequest request) {

        if(registerRepository.existsByEmail(request.getEmail())){
            User existingUser = registerRepository.findByEmail(request.getEmail());
            return UserResponse.builder()
                    .id(existingUser.getId())
                    .keyCloakId(existingUser.getKeyCloakId())
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .email(existingUser.getEmail())
                    .password(existingUser.getPassword())
                    .createdAt(existingUser.getCreatedAt())
                    .updatedAt(existingUser.getUpdatedAt())
                    .build();
        }
            User user = User.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())     // change in hashed later
                    .keyCloakId(request.getKeyCloakId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .role(UserRole.USER)
                    .build();

            User savedUser = registerRepository.save(user);

            return UserResponse.builder()
                    .id(savedUser.getId())
                    .keyCloakId(savedUser.getKeyCloakId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .email(savedUser.getEmail())
                    .password(savedUser.getPassword())
                    .createdAt(savedUser.getCreatedAt())
                    .updatedAt(savedUser.getUpdatedAt())
                    .build();
    }

    @Override
    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        return registerRepository.existsByKeyCloakId(userId);
    }
}
