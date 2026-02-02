package io.smartcare.platform.device.service;

import io.smartcare.platform.device.dto.TelemetryData;
import io.smartcare.platform.device.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private DeviceService deviceService;

    /**
     * Initializes test mocks before each test execution.
     */
    @BeforeEach
    void setUp() {
        // RedisTemplate requires mocking its operations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /**
     * Tests that telemetry data is correctly processed, cached in Redis, and published to RabbitMQ.
     */
    @Test
    void shouldProcessTelemetryCorrectly() {
        // Given
        TelemetryData data = new TelemetryData("SN-123", 1L, 75.0, "BPM", LocalDateTime.now());

        // When
        deviceService.processTelemetry(data);

        // Then
        // Verify Redis interaction
        verify(valueOperations).set(eq("device:last:SN-123"), eq(data), any());
        
        // Verify RabbitMQ interaction
        verify(rabbitTemplate).convertAndSend(eq("internal.exchange"), eq("device.telemetry.update"), eq(data));
    }
}
