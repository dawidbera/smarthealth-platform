package io.smartcare.platform.appointment.service;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final WebClient.Builder webClientBuilder;

    @Value("${services.patient-service.url}")
    private String patientServiceUrl;

    @Transactional
    public Appointment bookAppointment(Appointment appointment) {
        log.info("Checking patient existence for ID: {}", appointment.getPatientId());

        Boolean patientExists = webClientBuilder.build()
                .get()
                .uri(patientServiceUrl + "/patient/{id}/exists", appointment.getPatientId())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block(); // Synchronous call for simple verification

        if (Boolean.FALSE.equals(patientExists)) {
            throw new IllegalArgumentException("Cannot book appointment. Patient with ID " + appointment.getPatientId() + " does not exist.");
        }

        appointment.setStatus(AppointmentStatus.BOOKED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Appointment booked with ID: {}", savedAppointment.getId());
        
        rabbitTemplate.convertAndSend("internal.exchange", "appointment.booked", "Appointment booked with ID: " + savedAppointment.getId());
        
        return savedAppointment;
    }
}