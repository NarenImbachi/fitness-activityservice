package com.fitness.activityservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ResponseActivity;
import com.fitness.activityservice.service.ActivityService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;
    
    @PostMapping
    public ResponseEntity<ResponseActivity> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-ID") String userId) {

        if(userId != null) {
            request.setUserId(userId);
        }

        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping()
    public ResponseEntity<List<ResponseActivity>> getUserActivity(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(activityService.getUserActivity(userId));
    }
    
    @GetMapping("/{activityId}")
    public ResponseEntity<ResponseActivity> getActivity(@PathVariable String activityId) {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }
}
