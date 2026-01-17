package io.smartcare.platform.device.controller;

import io.smartcare.platform.device.dto.TelemetryData;
import io.smartcare.platform.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/telemetry")
    public ResponseEntity<Void> receiveTelemetry(@RequestBody TelemetryData data) {
        deviceService.processTelemetry(data);
        return ResponseEntity.accepted().build();
    }
}
