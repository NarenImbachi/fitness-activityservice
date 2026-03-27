package com.fitness.activityservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fitness.activityservice.enums.ActivityType;

import lombok.Data;

@Data
public class ResponseActivity {
    private String id;
    private String userId;
    private ActivityType type;
    private Integer duration; // in minutes
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics; // For any extra info like distance, steps, etc.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
