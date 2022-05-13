package com.ajmanre.payload;

import com.ajmanre.models.Activity;
import com.ajmanre.payload.request.ActivityRequest;
import com.ajmanre.payload.response.ActivityResponse;
import com.ajmanre.payload.response.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;

@Log4j2
public class Converter {

    public static ActivityResponse toPayload(Activity activity) {
        ActivityResponse payload = new ActivityResponse();
        BeanUtils.copyProperties(activity, payload);
        return payload;
    }

    public static Activity fromPayload(ActivityRequest activityRequest) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityRequest, activity);
        return activity;
    }

    public static User toPayload(com.ajmanre.models.User user) {
        User payload = new User();
        BeanUtils.copyProperties(user, payload);
        return payload;
    }
}
