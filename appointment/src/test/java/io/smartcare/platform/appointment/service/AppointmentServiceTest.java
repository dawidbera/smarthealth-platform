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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        testAppointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(101L)
                .appointmentTime(LocalDateTime.now())
                .status(AppointmentStatus.REQUESTED)
                .build();

        // Mocking the fluent WebClient API
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void bookAppointment_ShouldSucceed_WhenPatientExists() {
        // Given
        mockWebClientReturn(true);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment result = appointmentService.bookAppointment(testAppointment);

        // Then
        assertNotNull(result);
        assertEquals(AppointmentStatus.BOOKED, result.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(rabbitTemplate).convertAndSend(eq("internal.exchange"), eq("appointment.booked"), anyString());
    }

    @Test
    void bookAppointment_ShouldThrowException_WhenPatientDoesNotExist() {
        // Given
        mockWebClientReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.bookAppointment(testAppointment);
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientReturn(boolean exists) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(exists));
    }
}