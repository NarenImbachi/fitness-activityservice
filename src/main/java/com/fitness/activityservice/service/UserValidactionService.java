package com.fitness.activityservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidactionService {
    private final WebClient userServiceWebClient;

    public boolean isUserValid(String userId) {
        try {
            // Realiza una solicitud GET al User Service para verificar si el usuario existe
            return userServiceWebClient.get()
                    .uri("/api/users/{id}/validate", userId)
                    .retrieve() // Recupera la respuesta de la solicitud
                    .bodyToMono(Boolean.class) // Convierte el cuerpo de la respuesta a un Mono<Boolean>
                    .block(); // Bloquea hasta obtener la respuesta
        } catch (WebClientResponseException e) {
            // Si ocurre un error (como 404), consideramos que el usuario no es válido
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) 
                throw new RuntimeException("User not found");
            else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new RuntimeException("Invalid user ID format");
            else
                throw new RuntimeException("An error occurred while validating the user");

        }
    }
}
