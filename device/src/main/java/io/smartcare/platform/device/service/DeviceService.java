package io.smartcare.platform.device.service;

import io.smartcare.platform.device.dto.TelemetryData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

@Service
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public DeviceService(RedisTemplate<String, Object> redisTemplate, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Processes incoming telemetry data from a device.
     * Caches the latest data in Redis and publishes an event to RabbitMQ.
     * 
     * @param data the telemetry data to process
     */
    public void processTelemetry(TelemetryData data) {
        log.info("Processing telemetry from device {}: {} {}", 
            data.serialNumber(), data.value(), data.unit());

        String redisKey = "device:last:" + data.serialNumber();
        redisTemplate.opsForValue().set(redisKey, data, Duration.ofHours(24));

        rabbitTemplate.convertAndSend("internal.exchange", "device.telemetry.update", data);
    }
}
