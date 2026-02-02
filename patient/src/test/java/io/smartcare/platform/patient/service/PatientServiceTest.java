package io.smartcare.platform.patient.service;

import io.smartcare.platform.patient.config.RabbitMQConfig;
import io.smartcare.platform.patient.domain.Patient;
import io.smartcare.platform.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;

    /**
     * Initializes test data before each test execution.
     */
    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .nationalId("12345678901")
                .email("john.doe@example.com")
                .build();
    }

    /**
     * Tests that a patient is successfully registered when their national ID is unique.
     */
    @Test
    void registerPatient_ShouldSucceed_WhenNationalIdIsUnique() {
        // Given
        when(patientRepository.findByNationalId(anyString())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // When
        Patient savedPatient = patientService.registerPatient(testPatient);

        // Then
        assertNotNull(savedPatient);
        assertEquals("John", savedPatient.getFirstName());
        verify(patientRepository).save(any(Patient.class));
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EXCHANGE), eq(RabbitMQConfig.ROUTING_KEY_PATIENT_CREATED), anyString());
    }

    /**
     * Tests that patient registration throws an exception when a patient with the same national ID already exists.
     */
    @Test
    void registerPatient_ShouldThrowException_WhenNationalIdExists() {
        // Given
        when(patientRepository.findByNationalId("12345678901")).thenReturn(Optional.of(testPatient));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.registerPatient(testPatient);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(patientRepository, never()).save(any(Patient.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
    }
}
