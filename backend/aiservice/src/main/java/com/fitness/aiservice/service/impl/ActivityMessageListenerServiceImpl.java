package com.fitness.aiservice.service.impl;

import com.fitness.aiservice.model.entity.Activity;
import com.fitness.aiservice.model.entity.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import com.fitness.aiservice.service.ActivityMessageListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivityMessageListenerServiceImpl implements ActivityMessageListenerService {

    private final ActivityAIServiceImpl activityAIServiceImpl;
    private final RecommendationRepository recommendationRepository;

    public ActivityMessageListenerServiceImpl(ActivityAIServiceImpl activityAIServiceImpl, RecommendationRepository recommendationRepository) {
        this.activityAIServiceImpl = activityAIServiceImpl;
        this.recommendationRepository = recommendationRepository;
    }

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        log.info("Received Activity for processing: {}", activity.getId());
//        log.info("Generate Recommendations: {}", activityAIServiceImpl.generateRecommendation(activity));
        Recommendation recommendation = activityAIServiceImpl.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }




}
