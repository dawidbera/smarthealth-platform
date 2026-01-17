package io.smartcare.platform.appointment.dto;

import java.time.LocalDateTime;

public record BookAppointmentRequest(
    Long patientId,
    Long doctorId,
    LocalDateTime appointmentTime
) {}
