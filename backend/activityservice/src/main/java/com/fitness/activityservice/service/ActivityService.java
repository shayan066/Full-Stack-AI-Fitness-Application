package com.fitness.activityservice.service;

import com.fitness.activityservice.config.APILogger;
import com.fitness.activityservice.model.request.ActivityRequest;
import com.fitness.activityservice.model.request.UserActivityUpdateRequest;
import com.fitness.activityservice.model.response.ActivityResponse;
import com.fitness.activityservice.model.response.UserActivityResponse;

import com.fitness.activityservice.model.response.UserDetailUpdateResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ActivityService {

    ActivityResponse trackActivity(ActivityRequest request, APILogger logger);

    List<ActivityResponse> getActivityResponse(String userId, APILogger logger);

    UserActivityResponse getAllUserData(Pageable pageable, APILogger logger);

    UserDetailUpdateResponse updateUserData(UserActivityUpdateRequest request, String userId);

    Boolean existByUserId(String userId);
}
