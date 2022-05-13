package com.ajmanre.repository;

import com.ajmanre.models.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {

    public Activity findByName(String name);
}
