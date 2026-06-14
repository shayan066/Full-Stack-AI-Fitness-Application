package com.fitness.aiservice.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRecommendationResponse {
    private String message;
    private String data;
}
