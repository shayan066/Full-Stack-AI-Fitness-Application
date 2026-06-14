package com.fitness.aiservice.controller;

import com.fitness.aiservice.model.entity.Recommendation;
import com.fitness.aiservice.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation/")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendations(@PathVariable String userId){
         return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));
    }

//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping
//    public UserRecommendationResponse getAllUserData(@PageableDefault(size = 10) Pageable pageable) {
//        UserRecommendationResponse response = null;
//
//        APILogger logger = new APILogger("GET", "/api/activities/");
//
//        try {
//            logger.add(ApplicationConstants.START_TIME, String.valueOf(LocalDateTime.now()));
//            response = recommendationService.getUserRecommendation(pageable, logger);
//            logger.add(ApplicationConstants.END_TIME, String.valueOf(LocalDateTime.now()));
//            logger.logSuccess(200);
//        } catch (DataFetchException e) {
//            logger.logError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
//            throw e;
//        } catch (ActivityNotFoundException e) {
//            logger.logError(e.getMessage(), HttpStatus.NOT_FOUND.value());
//            throw e;
//        } catch (Exception e) {
//            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
//            throw e;
//        }
//        return response;
//    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendations(@PathVariable String activityId){
        return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
    }
}
