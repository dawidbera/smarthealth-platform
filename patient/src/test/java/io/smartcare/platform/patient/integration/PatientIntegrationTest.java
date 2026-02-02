package io.smartcare.platform.patient.integration;

import io.smartcare.platform.patient.domain.Patient;
import io.smartcare.platform.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Patient Service.
 * Tests end-to-end patient registration and retrieval flows.
 * Uses Testcontainers for PostgreSQL and RabbitMQ with a live application context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PatientIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Integration test: Verifies complete patient registration flow from API to database.
     * Creates a patient via POST endpoint and verifies it's persisted in PostgreSQL.
     */
    @Test
    void shouldRegisterAndRetrievePatient() {
        // Given
        Patient patient = Patient.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .nationalId("SN123456789")
                .build();

        // When
        ResponseEntity<Patient> response = restTemplate.postForEntity("/patient", patient, Patient.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        // Verify in DB
        assertThat(patientRepository.findById(response.getBody().getId())).isPresent();
    }
}
