package com.fitness.activityservice.repository;

import com.fitness.activityservice.model.entities.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {

    List<Activity> findAllByUserId(String userId);

    Optional<Activity> findByUserId(String userId);
}
