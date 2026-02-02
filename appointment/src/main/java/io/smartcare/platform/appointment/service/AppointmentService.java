package io.smartcare.platform.appointment.service;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PatientServiceClient patientServiceClient;

    /**
     * Books a new appointment for a patient.
     * 
     * @param appointment the appointment details to be booked
     * @return the saved appointment with status BOOKED
     * @throws IllegalArgumentException if the patient does not exist
     */
    @Transactional
    public Appointment bookAppointment(Appointment appointment) {
        log.info("Checking patient existence for ID: {}", appointment.getPatientId());

        boolean patientExists = patientServiceClient.checkPatientExistence(appointment.getPatientId());

        if (!patientExists) {
            throw new IllegalArgumentException("Cannot book appointment. Patient with ID " + appointment.getPatientId() + " does not exist.");
        }

        appointment.setStatus(AppointmentStatus.BOOKED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Appointment booked with ID: {}", savedAppointment.getId());
        
        rabbitTemplate.convertAndSend("internal.exchange", "appointment.booked", "Appointment booked with ID: " + savedAppointment.getId());
        
        return savedAppointment;
    }
}