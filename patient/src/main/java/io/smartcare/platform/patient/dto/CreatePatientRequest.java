package io.smartcare.platform.patient.dto;

public record CreatePatientRequest(
    String firstName,
    String lastName,
    String email,
    String pesel
) {}
