package com.fitness.activityservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public UserValidationService(WebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    public Boolean validateUser(String userId){
        return userServiceWebClient.get()
                .uri("")
    }
}
