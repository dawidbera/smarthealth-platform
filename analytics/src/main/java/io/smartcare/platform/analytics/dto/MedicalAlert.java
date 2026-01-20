package io.smartcare.platform.analytics.dto;

import java.time.LocalDateTime;

public record MedicalAlert(
    Long patientId,
    String type,
    String message,
    Double lastValue,
    LocalDateTime timestamp
) {}
