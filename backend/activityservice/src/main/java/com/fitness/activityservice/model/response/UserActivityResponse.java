package com.fitness.activityservice.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserActivityResponse {
    private String status;
    private String message;
    private List<ActivityResponse> data;
    private int page;
    private int size;
    private long totalElement;
    private int totalPages;
}
