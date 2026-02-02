package io.smartcare.platform.analytics.service;

import io.smartcare.platform.analytics.dto.MedicalAlert;
import io.smartcare.platform.analytics.dto.TelemetryData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final RabbitTemplate rabbitTemplate;
    
    // Prosta mapa w pamięci: PatientId -> Lista ostatnich 3 odczytów
    private final Map<Long, List<Double>> recentReadings = new ConcurrentHashMap<>();

    /**
     * Analyzes telemetry data for potential medical anomalies.
     * Tracks the last few readings for each patient to detect trends.
     * 
     * @param data the telemetry data to analyze
     */
    public void analyze(TelemetryData data) {
        if (!"BPM".equalsIgnoreCase(data.unit())) {
            return;
        }

        Long patientId = data.patientId();
        if (patientId == null) return;

        List<Double> readings = recentReadings.computeIfAbsent(patientId, k -> new ArrayList<>());
        readings.add(data.value());

        if (readings.size() > 3) {
            readings.remove(0);
        }

        if (readings.size() == 3 && isAnomalous(readings)) {
            sendAlert(data, readings);
            // Czyścimy, żeby nie wysyłać alertu przy każdym kolejnym wysokim odczycie
            readings.clear();
        }
    }

    /**
     * Determines if a series of readings is considered anomalous.
     * Currently detects 3 consecutive readings above 120 BPM.
     * 
     * @param readings the list of recent heart rate readings
     * @return true if an anomaly is detected, false otherwise
     */
    private boolean isAnomalous(List<Double> readings) {
        // Reguła: 3 kolejne odczyty powyżej 120 BPM
        return readings.stream().allMatch(v -> v > 120.0);
    }

    /**
     * Sends a medical alert event when an anomaly is detected.
     * 
     * @param data the current telemetry data triggering the alert
     * @param readings the sequence of readings that formed the anomaly
     */
    private void sendAlert(TelemetryData data, List<Double> readings) {
        log.warn("🚨 ANOMALY DETECTED for patient {}: {} BPM", data.patientId(), data.value());
        
        MedicalAlert alert = new MedicalAlert(
            data.patientId(),
            "CRITICAL_HEART_RATE",
            "Consistent high heart rate detected: " + readings,
            data.value(),
            LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend("internal.exchange", "medical.alert", alert);
    }
}
