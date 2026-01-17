package io.smartcare.platform.device.service;

import io.smartcare.platform.device.dto.TelemetryData;
import io.smartcare.platform.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public void processTelemetry(TelemetryData data) {
        log.info("Processing telemetry for device: {}", data.serialNumber());

        // 1. Save to Redis (Last Known Value cache) - valid for 24h
        String redisKey = "device:last:" + data.serialNumber();
        redisTemplate.opsForValue().set(redisKey, data, Duration.ofHours(24));

        // 2. Send to RabbitMQ for Analytics/History
        rabbitTemplate.convertAndSend("internal.exchange", "device.telemetry.update", data);
        
        log.debug("Telemetry cached and event sent for {}", data.serialNumber());
    }
}
