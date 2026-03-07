package com.fitness.userservice.service.impl;

import com.fitness.userservice.model.entity.User;
import com.fitness.userservice.model.enums.UserRole;
import com.fitness.userservice.model.request.RegisterRequest;
import com.fitness.userservice.model.response.UserResponse;
import com.fitness.userservice.repository.RegisterRepository;
import com.fitness.userservice.service.UserService;
import org.springframework.stereotype.Service;

@Service
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

        if(registerRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email Already Exists");
        }
            User user = User.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())     // change in hashed later
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .role(UserRole.USER)
                    .build();

            User savedUser = registerRepository.save(user);

            return UserResponse.builder()
                    .id(savedUser.getId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getFirstName())
                    .email(savedUser.getEmail())
                    .password(savedUser.getPassword())
                    .build();
    }
}
