package io.smartcare.platform.device;

import io.smartcare.platform.device.dto.TelemetryData;
import io.smartcare.platform.device.repository.DeviceRepository;
import io.smartcare.platform.device.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class ContractVerifierBase {

    @Autowired
    private DeviceService deviceService;

    @MockBean
    private DeviceRepository deviceRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    public void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    public void triggerTelemetryUpdate() {
        TelemetryData data = new TelemetryData(
                "SN-CONTRACT-001",
                85.0,
                "BPM",
                LocalDateTime.parse("2026-01-18T10:00:00")
        );
        deviceService.processTelemetry(data);
    }
}
