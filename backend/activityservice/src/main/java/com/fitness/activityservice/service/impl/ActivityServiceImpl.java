package com.fitness.activityservice.service.impl;

import com.fitness.activityservice.config.APILogger;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationServiceImpl userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityServiceImpl(ActivityRepository activityRepository, UserValidationServiceImpl userValidationService, RabbitTemplate rabbitTemplate) {
        this.activityRepository = activityRepository;
        this.userValidationService = userValidationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ActivityResponse trackActivity(ActivityRequest request, APILogger logger) {

        Boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if(!isValidUser){
            throw new RuntimeException("Invalid User: "+ request.getUserId());
        }
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

//        Publish to RabbitMq for AI Processing
        try{
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to rabbitMQ: ", e);
        }

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

    @Override
    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        return activityRepository.existsById(userId);
    }
}
