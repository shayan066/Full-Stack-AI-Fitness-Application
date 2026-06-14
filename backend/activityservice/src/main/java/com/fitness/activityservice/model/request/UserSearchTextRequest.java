package com.fitness.activityservice.model.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserSearchTextRequest {
    private String searchText;
    private LocalDate startDate;
    private LocalDate endDate;
}
