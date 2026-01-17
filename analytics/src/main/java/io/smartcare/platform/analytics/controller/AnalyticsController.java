package io.smartcare.platform.analytics.controller;

import io.smartcare.platform.analytics.domain.TelemetryRecord;
import io.smartcare.platform.analytics.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final TelemetryRepository telemetryRepository;

    @GetMapping("/device/{serialNumber}")
    public List<TelemetryRecord> getDeviceHistory(@PathVariable String serialNumber) {
        return telemetryRepository.findBySerialNumber(serialNumber);
    }
}
