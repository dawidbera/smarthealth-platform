package io.smartcare.platform.device.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record TelemetryData(
    String serialNumber,
    Double value,
    String unit,
    LocalDateTime timestamp
) implements Serializable {}
