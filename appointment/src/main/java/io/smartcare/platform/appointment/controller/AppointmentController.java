package io.smartcare.platform.appointment.controller;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.dto.BookAppointmentRequest;
import io.smartcare.platform.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import io.smartcare.platform.appointment.repository.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    /**
     * Retrieves all booked appointments.
     * 
     * @return a list of all appointments
     */
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    /**
     * Books a new appointment.
     * 
     * @param request the appointment booking request
     * @return the created appointment with status 201 Created
     */
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
