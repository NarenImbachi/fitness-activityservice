package com.fitness.activityservice.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ResponseActivity;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repo.ActivityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidactionService userValidactionService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ResponseActivity trackActivity(ActivityRequest request) {

        boolean isValidUser = userValidactionService.isUserValid(request.getUserId());

        if (!isValidUser) 
            throw new RuntimeException("Invalid user ID: " + request.getUserId());

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
        
        Activity savedActivity = activityRepository.save(activity);

        //Publish to RabbitMQ for IA Processing
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ: " + e.getMessage());
        }

        return mapToResponse(savedActivity);
    }

    private ResponseActivity mapToResponse(Activity activity) {
        ResponseActivity response = new ResponseActivity();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }

    public List<ResponseActivity> getUserActivity(String id) {
        List<Activity> activities = activityRepository.findByUserId(id);
        return activities.stream().map(this::mapToResponse).toList();
    }

    public ResponseActivity getActivityById(String activityId) {
        activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found"));
        
        return mapToResponse(activityRepository.findById(activityId).get());
    }
    
}
