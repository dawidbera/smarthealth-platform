package io.smartcare.platform.device;

import io.smartcare.platform.device.dto.TelemetryData;
import io.smartcare.platform.device.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class DeviceController {

    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);
    private final DeviceService deviceService;
    private final RedisTemplate<String, Object> redisTemplate;

    public DeviceController(DeviceService deviceService, RedisTemplate<String, Object> redisTemplate) {
        this.deviceService = deviceService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Receives telemetry data from a device and triggers processing.
     * 
     * @param data the telemetry data to receive
     * @return 200 OK
     */
    @PostMapping("/telemetry")
    public ResponseEntity<Void> receiveTelemetry(@RequestBody TelemetryData data) {
        log.info("Received POST /telemetry for SN: {}", data.serialNumber());
        deviceService.processTelemetry(data);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the most recent telemetry data for a specific device from the cache.
     * 
     * @param serialNumber the serial number of the device
     * @return the latest telemetry data if found
     */
    @GetMapping("/telemetry/{serialNumber}/latest")
    public ResponseEntity<Object> getLatestTelemetry(@PathVariable String serialNumber) {
        log.info("Received GET /telemetry/{}/latest", serialNumber);
        Object data = redisTemplate.opsForValue().get("device:last:" + serialNumber);
        return ResponseEntity.ok(data);
    }
}