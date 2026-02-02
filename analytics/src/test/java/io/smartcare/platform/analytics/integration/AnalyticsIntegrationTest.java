package io.smartcare.platform.analytics.integration;

import io.smartcare.platform.analytics.dto.TelemetryData;
import io.smartcare.platform.analytics.repository.TelemetryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for the Analytics Service.
 * Tests the complete flow from telemetry reception via RabbitMQ to statistics calculation via REST API.
 * Uses Testcontainers for PostgreSQL and RabbitMQ, with a live application context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AnalyticsIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TelemetryRepository telemetryRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Integration test: Verifies complete telemetry flow from RabbitMQ to statistics API.
     * Sends telemetry via RabbitMQ, waits for persistence, then queries stats endpoint.
     */
    @Test
    void shouldProcessTelemetryAndReturnStats() {
        // Given
        String serialNumber = "TEST-SN-IT";
        TelemetryData data = new TelemetryData(serialNumber, 1L, 80.0, "BPM", LocalDateTime.now());

        // When: Send telemetry event to RabbitMQ
        rabbitTemplate.convertAndSend("internal.exchange", "device.telemetry.update", data);

        // Then: Wait for it to be persisted
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(telemetryRepository.findAll())
                    .extracting("serialNumber")
                    .contains(serialNumber);
        });

        // Verify stats via API
        Map<String, Object> stats = restTemplate.getForObject("/analytics/device/" + serialNumber + "/stats", Map.class);
        assertThat(stats.get("average")).isEqualTo(80.0);
        assertThat(stats.get("count")).isEqualTo(1);
    }
}
