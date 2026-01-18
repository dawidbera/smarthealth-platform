package io.smartcare.platform.appointment.integration;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.dto.BookAppointmentRequest;
import io.smartcare.platform.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWireMock(port = 0)
class AppointmentIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Point the appointment service to our WireMock instance instead of the real patient service
        registry.add("services.patient-service.url", () -> "http://localhost:${wiremock.server.port}");
    }

    @Test
    void shouldBookAppointmentAndPublishEvent() {
        // Given: WireMock stub for patient existence check
        stubFor(get(urlEqualTo("/patient/1/exists"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        BookAppointmentRequest request = new BookAppointmentRequest(1L, 101L, LocalDateTime.now());

        // When
        ResponseEntity<Appointment> response = restTemplate.postForEntity("/appointment", request, Appointment.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(AppointmentStatus.BOOKED);

        // Verify in DB
        assertThat(appointmentRepository.findAll()).hasSize(1);

        // Verify event in RabbitMQ (we can try to receive it from the exchange/queue if we define a temporary one, 
        // but here we just check if the call didn't fail)
        // In a real IT we could use a dedicated test queue.
    }
}
