package io.smartcare.platform.analytics.controller;

import io.smartcare.platform.analytics.domain.TelemetryRecord;
import io.smartcare.platform.analytics.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final TelemetryRepository telemetryRepository;

    /**
     * Retrieves the telemetry history for a specific device.
     * 
     * @param serialNumber the serial number of the device
     * @param limit the maximum number of records to return (defaults to 10)
     * @return a list of telemetry records sorted by timestamp descending
     */
    @GetMapping("/device/{serialNumber}/history")
    public List<TelemetryRecord> getDeviceHistory(
            @PathVariable String serialNumber,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching history for device: {}, limit: {}", serialNumber, limit);
        try {
            return telemetryRepository.findBySerialNumber(
                    serialNumber, 
                    PageRequest.of(0, limit, Sort.by("timestamp").descending())
            );
        } catch (Exception e) {
            log.error("Error fetching history: ", e);
            throw e;
        }
    }

    /**
     * Calculates and retrieves statistics for a specific device based on recent telemetry.
     * 
     * @param serialNumber the serial number of the device
     * @return a map containing average value, maximum value, record count, and health status
     */
    @GetMapping("/device/{serialNumber}/stats")
    public Map<String, Object> getDeviceStats(@PathVariable String serialNumber) {
        log.info("Fetching stats for device: {}", serialNumber);
        try {
            List<TelemetryRecord> records = telemetryRepository.findBySerialNumber(
                    serialNumber,
                    PageRequest.of(0, 100, Sort.by("timestamp").descending())
            );

            Map<String, Object> stats = new HashMap<>();
            if (records.isEmpty()) {
                stats.put("average", 0.0);
                stats.put("max", 0.0);
                stats.put("count", 0);
                stats.put("status", "No data");
                return stats;
            }

            double avg = records.stream()
                    .mapToDouble(TelemetryRecord::getValue)
                    .average()
                    .orElse(0.0);
            
            double max = records.stream()
                    .mapToDouble(TelemetryRecord::getValue)
                    .max()
                    .orElse(0.0);

            stats.put("average", Math.round(avg * 10.0) / 10.0);
            stats.put("max", max);
            stats.put("count", records.size());
            stats.put("status", avg > 100 ? "Warning: High Heart Rate" : "Normal");
            
            return stats;
        } catch (Exception e) {
            log.error("Error calculating stats: ", e);
            throw e;
        }
    }
}
