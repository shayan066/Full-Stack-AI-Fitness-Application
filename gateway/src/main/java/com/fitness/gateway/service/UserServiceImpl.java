package com.fitness.activityservice.service.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
public class UserValidationServiceImpl {
    private final WebClient userServiceWebClient;

    public UserValidationServiceImpl(WebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    public Boolean validateUser(String userId){
        log.info("Calling User Validation API for userId: {}", userId);
        try{
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }
        catch (WebClientResponseException e){
            if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new RuntimeException("User not found: " + userId);
            else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new BadRequestException("Invalid Request: "+ userId);
            else if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                throw new InternalServerErrorException("Internal Server Error");
        }
        return false;
    }
}
