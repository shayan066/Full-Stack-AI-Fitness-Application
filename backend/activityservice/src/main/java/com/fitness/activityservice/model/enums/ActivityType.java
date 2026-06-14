package com.fitness.activityservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ActivityType {
    RUNNING,
    WALKING,
    CYCLING,
    SWIMMING,
    WEIGHT_TRAINING,
    YOGA,
    CARDIO,
    STRETCHING;

    @JsonCreator
    public static ActivityType from(String value) {
        try {
            return ActivityType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid activityType. Allowed values: RUNNING, WALKING, CYCLING, SWIMMING, WEIGHT_TRAINING, YOGA, CARDIO, STRETCHING"
            );
        }
    }
}
