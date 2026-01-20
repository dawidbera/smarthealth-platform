package io.smartcare.platform.device.dto;

import java.time.LocalDateTime;

public record TelemetryData(
    String serialNumber,
    Long patientId,
    Double value,
    String unit,
    LocalDateTime timestamp
) {}
