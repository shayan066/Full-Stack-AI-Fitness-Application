package com.fitness.activityservice.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailUpdateResponse {
    private String message;
    private ActivityResponse data;
}
