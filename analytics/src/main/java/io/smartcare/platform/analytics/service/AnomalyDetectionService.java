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

    private boolean isAnomalous(List<Double> readings) {
        // Reguła: 3 kolejne odczyty powyżej 120 BPM
        return readings.stream().allMatch(v -> v > 120.0);
    }

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
