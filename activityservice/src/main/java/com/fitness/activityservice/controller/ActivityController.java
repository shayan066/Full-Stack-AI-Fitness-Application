package com.fitness.activityservice.controller;

import com.fitness.activityservice.configs.APILogger;
import com.fitness.activityservice.exceptions.ActivityNotFoundException;
import com.fitness.activityservice.exceptions.DataFetchException;
import com.fitness.activityservice.model.request.ActivityRequest;
import com.fitness.activityservice.model.request.UserActivityUpdateRequest;
import com.fitness.activityservice.model.response.UserActivityResponse;
import com.fitness.activityservice.model.response.UserDetailUpdateResponse;
import com.fitness.activityservice.service.ActivityService;
import com.fitness.activityservice.utils.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public UserActivityResponse getAllUserData(@PageableDefault(size = 10) Pageable pageable) {
        UserActivityResponse response = null;

        APILogger logger = new APILogger("GET", "/api/activities/");

        try {
            logger.add(ApplicationConstants.START_TIME, String.valueOf(LocalDateTime.now()));
            response = activityService.getAllUserData(pageable, logger);
            logger.add(ApplicationConstants.END_TIME, String.valueOf(LocalDateTime.now()));
            logger.logSuccess(200);
        } catch (DataFetchException e) {
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        } catch (ActivityNotFoundException e) {
            logger.logError(e.getMessage(), HttpStatus.NOT_FOUND.value());
            throw e;
        } catch (Exception e) {
            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public Object trackActivity(@Validated @RequestBody ActivityRequest request) {
        Object response = null;

        APILogger logger = new APILogger("POST", "/api/activities/");
        try {
            logger.add(ApplicationConstants.START_TIME, String.valueOf(LocalDateTime.now()));
            response = activityService.trackActivity(request, logger);
            logger.add(ApplicationConstants.END_TIME, String.valueOf(LocalDateTime.now()));
            logger.logSuccess(200);
        } catch (DataFetchException e) {
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        }
        catch (Exception e) {
            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
        return response;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public Object getUserActivities(@PathVariable String userId) {
        Object response = null;

        APILogger logger = new APILogger("GET", "/api/activities" + userId);

        try {
            logger.add(ApplicationConstants.START_TIME, String.valueOf(LocalDateTime.now()));
            response = activityService.getActivityResponse(userId, logger);
            logger.add(ApplicationConstants.END_TIME, String.valueOf(LocalDateTime.now()));
            logger.logSuccess(200);
        } catch (DataFetchException e) {
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        } catch (ActivityNotFoundException e) {
            logger.logError(e.getMessage(), HttpStatus.NOT_FOUND.value());
            throw e;
        } catch (Exception e) {
            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update")
    public UserDetailUpdateResponse updateUserDetails(@RequestBody UserActivityUpdateRequest request, @RequestParam String userId){
        UserDetailUpdateResponse response = null;
        APILogger logger = new APILogger("PUT", "/api/activities/update"+ userId);
        try{
            logger.add(ApplicationConstants.START_TIME, String.valueOf(LocalDateTime.now()));
            response = activityService.updateUserData(request, userId);
            logger.add(ApplicationConstants.END_TIME, String.valueOf(LocalDateTime.now()));
            logger.logSuccess(200);
        } catch (DataFetchException e) {
            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            throw e;
        } catch (ActivityNotFoundException e) {
            logger.logError(e.getMessage(), HttpStatus.NOT_FOUND.value());
            throw e;
        } catch (Exception e) {
            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
        return response;
    }


}
