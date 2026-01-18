package io.smartcare.platform.device.integration;

import io.smartcare.platform.device.dto.TelemetryData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class DeviceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Test
    void shouldStoreTelemetryInRedisAndReturnOk() {
        // Given
        String sn = "SN-IT-999";
        TelemetryData data = new TelemetryData(sn, 72.0, "BPM", LocalDateTime.now());

        // When
        ResponseEntity<Void> response = restTemplate.postForEntity("/telemetry", data, Void.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify in Redis
        Object cachedData = redisTemplate.opsForValue().get("device:last:" + sn);
        assertThat(cachedData).isNotNull();
        // Since it's stored as JSON/Object, we just check existence for simplicity here
    }
}
