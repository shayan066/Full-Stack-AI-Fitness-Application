package com.fitness.activityservice.model.request;

import com.fitness.activityservice.model.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityUpdateRequest {
    private String firstAndLastName;
    private ActivityType activityType;
    private Integer duration;
    private Integer caloriesBurned;
    private Map<String, Object> additionalMetrics;
}
