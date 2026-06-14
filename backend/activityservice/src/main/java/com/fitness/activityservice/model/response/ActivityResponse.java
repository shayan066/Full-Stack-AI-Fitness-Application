package com.fitness.activityservice.model.response;

import com.fitness.activityservice.model.enums.ActivityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ActivityResponse {
    private String id;
    private String userId;
    private String firstAndLastName;
    private ActivityType activityType;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
}
