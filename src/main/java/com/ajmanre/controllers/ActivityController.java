package com.ajmanre.controllers;

import com.ajmanre.payload.request.ActivityRequest;
import com.ajmanre.payload.response.ActivityResponse;
import com.ajmanre.payload.response.MessageResponse;
import com.ajmanre.services.ActivityService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<MessageResponse> activity(ActivityRequest request) {
        ActivityResponse activityResponse = activityService.create(request);
        log.info(activityResponse);
        return ResponseEntity.ok().body(new MessageResponse("Activity has been created"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> get(@PathVariable String id) {
        ActivityResponse activityResponse = activityService.get(id);
        log.info(activityResponse);
        return ResponseEntity.ok(activityResponse);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ActivityResponse> activity(@RequestParam String name) {
        ActivityResponse activityResponse = activityService.activity(name);
        log.info(activityResponse);
        return ResponseEntity.ok(activityResponse);
    }
}
