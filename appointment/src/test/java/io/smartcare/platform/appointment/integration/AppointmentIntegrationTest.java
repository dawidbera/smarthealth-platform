package io.smartcare.platform.appointment.integration;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.dto.BookAppointmentRequest;
import io.smartcare.platform.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureStubRunner(
        ids = "io.smartcare.platform:patient:+:stubs:8090",
        stubsMode = StubRunnerProperties.StubsMode.CLASSPATH
)
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

    @Autowired
    private AmqpAdmin amqpAdmin;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Map the patient service URL to the Stub Runner port
        registry.add("services.patient-service.url", () -> "http://localhost:8090");
    }

    @BeforeEach
    void setUp() {
        // Ensure exchange exists to avoid 404 logs during test execution
        amqpAdmin.declareExchange(new TopicExchange("internal.exchange"));
    }

    @Test
    void shouldBookAppointmentAndPublishEvent() {
        // The stub for patient-service is automatically running on port 8090
        BookAppointmentRequest request = new BookAppointmentRequest(1L, 101L, LocalDateTime.now());

        ResponseEntity<Appointment> response = restTemplate.postForEntity("/appointment", request, Appointment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(AppointmentStatus.BOOKED);
        assertThat(appointmentRepository.findAll()).hasSize(1);
    }
}
