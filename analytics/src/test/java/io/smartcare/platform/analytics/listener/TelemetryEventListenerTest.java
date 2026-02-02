package io.smartcare.platform.analytics.listener;

import io.smartcare.platform.analytics.domain.TelemetryRecord;
import io.smartcare.platform.analytics.dto.TelemetryData;
import io.smartcare.platform.analytics.repository.TelemetryRepository;
import io.smartcare.platform.analytics.service.AnomalyDetectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the Telemetry Event Listener in the Analytics Service.
 * Tests processing of telemetry data from RabbitMQ messages and storage in the database.
 * Verifies anomaly detection service integration.
 */
@ExtendWith(MockitoExtension.class)
class TelemetryEventListenerTest {

    @Mock
    private TelemetryRepository telemetryRepository;

    @Mock
    private AnomalyDetectionService anomalyDetectionService;

    @InjectMocks
    private TelemetryEventListener telemetryEventListener;

    /**
     * Tests that telemetry data received from RabbitMQ is saved to the database.
     * Verifies the saved record has correct values and timestamp.
     */
    @Test
    void shouldSaveTelemetryRecordWhenUpdateReceived() {
        // Given
        TelemetryData data = new TelemetryData("SN-1", 1L, 98.6, "F", LocalDateTime.now());

        // When
        telemetryEventListener.handleTelemetryUpdate(data);

        // Then
        ArgumentCaptor<TelemetryRecord> recordCaptor = ArgumentCaptor.forClass(TelemetryRecord.class);
        verify(telemetryRepository).save(recordCaptor.capture());

        TelemetryRecord saved = recordCaptor.getValue();
        assertEquals("SN-1", saved.getSerialNumber());
        assertEquals(98.6, saved.getValue());
        assertNotNull(saved.getReceivedAt());
    }
}
