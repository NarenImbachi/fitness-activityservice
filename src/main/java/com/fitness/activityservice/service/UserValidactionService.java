package com.fitness.activityservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidactionService {
    private final WebClient userServiceWebClient;

    public boolean isUserValid(String userId) {
        log.info("Calling user validation API for userId: {}", userId);
        try {
            // Realiza una solicitud GET al User Service para verificar si el usuario existe
            return userServiceWebClient.get()
                    .uri("/api/users/{id}/validate", userId)
                    .retrieve() // Recupera la respuesta de la solicitud
                    .bodyToMono(Boolean.class) // Convierte el cuerpo de la respuesta a un Mono<Boolean>
                    .block(); // Bloquea hasta obtener la respuesta
        } catch (WebClientResponseException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) 
                throw new RuntimeException("User not found");
            else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new RuntimeException("Invalid user ID format");
            else
                throw new RuntimeException("An error occurred while validating the user");

        }
    }
}
