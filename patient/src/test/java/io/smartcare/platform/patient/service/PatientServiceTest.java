package io.smartcare.platform.patient.service;

import io.smartcare.platform.patient.config.RabbitMQConfig;
import io.smartcare.platform.patient.domain.Patient;
import io.smartcare.platform.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldRegisterNewPatientSuccessfully() {
        // Given
        Patient patient = Patient.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .pesel("12345678901")
                .build();

        when(patientRepository.findByPesel(patient.getPesel())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> {
            Patient p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        Patient savedPatient = patientService.registerPatient(patient);

        // Then
        assertNotNull(savedPatient.getId());
        assertEquals("Jan", savedPatient.getFirstName());
        verify(patientRepository).save(any(Patient.class));
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EXCHANGE), eq(RabbitMQConfig.ROUTING_KEY_PATIENT_CREATED), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPeselAlreadyExists() {
        // Given
        Patient patient = Patient.builder().pesel("12345678901").build();
        when(patientRepository.findByPesel("12345678901")).thenReturn(Optional.of(patient));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> patientService.registerPatient(patient));
        verify(patientRepository, never()).save(any());
    }
}
