package com.fitness.activityservice.model.request;

import com.fitness.activityservice.model.enums.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {
    @NotBlank(message = "userId must not be blank")
    private String userId;

    @NotNull(message = "type must not be null")
    private ActivityType type;

    @NotNull(message = "Name should be not null")
    @NotBlank(message = "Name should be not blank")
    private String firstAndLastName;

    @NotNull(message = "duration is required")
    @Positive(message = "duration must be greater than 0")
    private Integer duration;

    @NotNull(message = "caloriesBurned is required")
    @PositiveOrZero(message = "caloriesBurned must be zero or positive")
    private Integer caloriesBurned;

    @NotNull(message = "startTime is required")
    private LocalDateTime startTime;

    @NotNull(message = "additionalMetrics must not be null")
    private Map<String, Object> additionalMetrics;
}
