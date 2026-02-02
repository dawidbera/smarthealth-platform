package io.smartcare.platform.analytics.listener;

import io.smartcare.platform.analytics.config.AnalyticsRabbitMQConfig;
import io.smartcare.platform.analytics.domain.TelemetryRecord;
import io.smartcare.platform.analytics.dto.TelemetryData;
import io.smartcare.platform.analytics.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelemetryEventListener {

    private final TelemetryRepository telemetryRepository;
    private final io.smartcare.platform.analytics.service.AnomalyDetectionService anomalyDetectionService;

    /**
     * Listens for telemetry update events and persists them to the repository.
     * Also triggers anomaly detection for the received data.
     * 
     * @param data the telemetry data received from the message queue
     */
    @RabbitListener(queues = AnalyticsRabbitMQConfig.QUEUE_ANALYTICS)
    public void handleTelemetryUpdate(TelemetryData data) {
        log.info("ANALYTICS RECEIVED: Data for device {} (Patient: {})", data.serialNumber(), data.patientId());

        TelemetryRecord record = TelemetryRecord.builder()
                .serialNumber(data.serialNumber())
                .patientId(data.patientId())
                .value(data.value())
                .unit(data.unit())
                .timestamp(data.timestamp())
                .receivedAt(LocalDateTime.now())
                .build();

        telemetryRepository.save(record);
        
        // Analiza anomalii
        anomalyDetectionService.analyze(data);
    }
}
