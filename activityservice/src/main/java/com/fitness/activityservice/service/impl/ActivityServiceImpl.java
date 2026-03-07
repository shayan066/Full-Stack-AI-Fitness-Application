package com.fitness.activityservice.service.impl;

import com.fitness.activityservice.configs.APILogger;
import com.fitness.activityservice.exceptions.ActivityNotFoundException;
import com.fitness.activityservice.exceptions.DataFetchException;
import com.fitness.activityservice.exceptions.UserNotFoundException;
import com.fitness.activityservice.model.entities.Activity;
import com.fitness.activityservice.model.request.ActivityRequest;
import com.fitness.activityservice.model.request.UserActivityUpdateRequest;
import com.fitness.activityservice.model.response.ActivityResponse;
import com.fitness.activityservice.model.response.UserActivityResponse;
import com.fitness.activityservice.model.response.UserDetailUpdateResponse;
import com.fitness.activityservice.repository.ActivityRepository;
import com.fitness.activityservice.service.ActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public ActivityResponse trackActivity(ActivityRequest request, APILogger logger) {
//        if(activityRepository.findById(request.getUserId()).isPresent()){
//            throw new RuntimeException("User Already Exists");
//        }
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .firstAndLastName(request.getFirstAndLastName())
                .activityType(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        return mapToResponse(savedActivity);
    }

    @Override
    public List<ActivityResponse> getActivityResponse(String userId, APILogger logger) {
        List<Activity> activities = activityRepository.findAllByUserId(userId);

        if(activities.isEmpty()){
            throw new ActivityNotFoundException("No activities are found for user: "+userId);
        }
        return activities.stream().map(activity -> ActivityResponse.builder()
                        .id(activity.getId())
                        .userId(activity.getUserId())
                        .firstAndLastName(activity.getFirstAndLastName())
                        .activityType(activity.getActivityType())
                        .duration(activity.getDuration())
                        .caloriesBurned(activity.getCaloriesBurned())
                        .startTime(activity.getStartTime())
                        .additionalMetrics(activity.getAdditionalMetrics())
                        .build()
            ).toList();
    }

    @Override
    public UserActivityResponse getAllUserData(Pageable pageable, APILogger logger) {
        Page<Activity> getAllData = activityRepository.findAll(pageable);

        List<ActivityResponse> activityResponses = getAllData.getContent().stream().map(this::mapToResponse).toList();

        return UserActivityResponse.builder()
                .status("SUCCESS")
                .message("All User Data Fetched Successfully!")
                .data(activityResponses)
                .page(getAllData.getNumber())
                .size(getAllData.getSize())
                .totalElement(getAllData.getTotalElements())
                .totalPages(getAllData.getTotalPages())
                .build();
    }

    @Override
    public UserDetailUpdateResponse updateUserData(UserActivityUpdateRequest request, String userId) {

        if(ObjectUtils.isEmpty(userId)){
            throw new UserNotFoundException("UserId required to update data");
        }

        Activity existId = activityRepository.findByUserId(userId).orElseThrow(() -> new DataFetchException("UserId not exists"));

        if(existId.getFirstAndLastName() == null && request.getFirstAndLastName() != null){
            existId.setFirstAndLastName(request.getFirstAndLastName());
        }
        if(existId.getActivityType() == null && request.getActivityType() != null){
            existId.setActivityType(request.getActivityType());
        }
        if(existId.getDuration() == null && request.getDuration() != null){
            existId.setDuration(request.getDuration());
        }
        if(existId.getCaloriesBurned() == null && request.getCaloriesBurned() != null){
            existId.setCaloriesBurned(request.getCaloriesBurned());
        }
        if(existId.getAdditionalMetrics() == null && request.getAdditionalMetrics() != null){
            existId.setAdditionalMetrics(request.getAdditionalMetrics());
        }

        Activity savedActivity = activityRepository.save(existId);

        return UserDetailUpdateResponse.builder()
                .message("Data Updated Successfully")
                .data(mapToResponse(savedActivity))
                .build();
    }

    private ActivityResponse mapToResponse(Activity activity){
        return ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .firstAndLastName(activity.getFirstAndLastName())
                .activityType(activity.getActivityType())
                .duration(activity.getDuration())
                .caloriesBurned(activity.getCaloriesBurned())
                .startTime(activity.getStartTime())
                .additionalMetrics(activity.getAdditionalMetrics())
                .build();
    }
}
