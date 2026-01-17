package io.smartcare.platform.appointment.controller;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.dto.BookAppointmentRequest;
import io.smartcare.platform.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> bookAppointment(@RequestBody BookAppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .appointmentTime(request.appointmentTime())
                .build();

        Appointment savedAppointment = appointmentService.bookAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
    }
}
