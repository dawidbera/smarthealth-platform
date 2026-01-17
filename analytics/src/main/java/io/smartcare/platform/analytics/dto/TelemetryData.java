package io.smartcare.platform.analytics.dto;

import java.time.LocalDateTime;

public record TelemetryData(
    String serialNumber,
    Double value,
    String unit,
    LocalDateTime timestamp
) {}
