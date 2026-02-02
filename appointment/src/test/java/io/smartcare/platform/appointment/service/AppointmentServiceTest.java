package io.smartcare.platform.appointment.service;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Appointment Service business logic.
 * Tests appointment booking including patient validation and event publishing.
 * Uses Mockito to mock external service calls and repositories.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PatientServiceClient patientServiceClient;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;

    /**
     * Initializes test data before each test execution.
     */
    @BeforeEach
    void setUp() {
        testAppointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(101L)
                .appointmentTime(LocalDateTime.now())
                .status(AppointmentStatus.REQUESTED)
                .build();
    }

    /**
     * Tests that an appointment is successfully booked when the patient exists.
     */
    @Test
    void bookAppointment_ShouldSucceed_WhenPatientExists() {
        // Given
        when(patientServiceClient.checkPatientExistence(1L)).thenReturn(true);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment result = appointmentService.bookAppointment(testAppointment);

        // Then
        assertNotNull(result);
        assertEquals(AppointmentStatus.BOOKED, result.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(rabbitTemplate).convertAndSend(eq("internal.exchange"), eq("appointment.booked"), anyString());
    }

    /**
     * Tests that booking an appointment throws an exception when the patient does not exist.
     */
    @Test
    void bookAppointment_ShouldThrowException_WhenPatientDoesNotExist() {
        // Given
        when(patientServiceClient.checkPatientExistence(1L)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.bookAppointment(testAppointment);
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
    }
}