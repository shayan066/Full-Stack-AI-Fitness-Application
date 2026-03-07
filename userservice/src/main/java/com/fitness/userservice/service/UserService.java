package com.fitness.userservice.service;

import com.fitness.userservice.model.request.RegisterRequest;
import com.fitness.userservice.model.response.UserResponse;

public interface UserService {

    UserResponse getUserProfile(String userId);

    UserResponse registerUser(RegisterRequest request);
}
