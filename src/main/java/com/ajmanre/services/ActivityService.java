package com.ajmanre.services;

import com.ajmanre.models.Activity;
import com.ajmanre.payload.Converter;
import com.ajmanre.payload.request.ActivityRequest;
import com.ajmanre.payload.response.ActivityResponse;
import com.ajmanre.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse create(ActivityRequest activityRequest) {
        Activity activity = Converter.fromPayload(activityRequest);
        Activity saved = activityRepository.save(activity);
        return Converter.toPayload(saved);
    }

    public ActivityResponse get(String id) {
        Activity activity = activityRepository.findById(id).get();
        return Converter.toPayload(activity);
    }

    public ActivityResponse activity(String name) {
        Activity activity = activityRepository.findByName(name);
        return Converter.toPayload(activity);
    }

}
