package io.smartcare.platform.appointment.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.patient-service.url}")
    private String patientServiceUrl;

    /**
     * Checks if a patient exists by calling the Patient microservice.
     * Wrapped with a Circuit Breaker to handle service unavailability.
     * 
     * @param patientId the ID of the patient to check
     * @return true if the patient exists, or based on fallback logic if service is unavailable
     */
    @CircuitBreaker(name = "patientService", fallbackMethod = "fallbackCheckPatientExistence")
    public boolean checkPatientExistence(Long patientId) {
        Boolean result = webClientBuilder.build()
                .get()
                .uri(patientServiceUrl + "/patient/{id}/exists", patientId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        return Boolean.TRUE.equals(result);
    }

    /**
     * Fallback method for patient existence check when the Patient Service is unavailable.
     * 
     * @param patientId the ID of the patient
     * @param t the exception that triggered the fallback
     * @return true as a default fallback strategy for this demo
     */
    public boolean fallbackCheckPatientExistence(Long patientId, Throwable t) {
        log.warn("Patient Service is down or returning error. Circuit Breaker fallback triggered for ID: {}. Error: {}", patientId, t.getMessage());
        // Fallback strategy: Assume patient exists to allow booking (or fail gracefully depending on business logic).
        // For this demo, let's assume valid to avoid blocking operations, but log it.
        return true; 
    }
}
