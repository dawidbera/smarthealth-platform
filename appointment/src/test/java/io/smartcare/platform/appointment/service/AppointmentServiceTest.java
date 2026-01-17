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
import static org.mockito.ArgumentMatchers.any;
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
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        // Fluent API mocking for WebClient
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void shouldBookAppointmentWhenPatientExists() {
        // Given
        Appointment appointment = Appointment.builder()
                .patientId(1L)
                .doctorId(10L)
                .appointmentTime(LocalDateTime.now())
                .build();

        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Appointment saved = appointmentService.bookAppointment(appointment);

        // Then
        assertEquals(AppointmentStatus.BOOKED, saved.getStatus());
        verify(appointmentRepository).save(any());
        verify(rabbitTemplate).convertAndSend(eq("internal.exchange"), eq("appointment.booked"), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPatientDoesNotExist() {
        // Given
        Appointment appointment = Appointment.builder().patientId(999L).build();
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> appointmentService.bookAppointment(appointment));
        verify(appointmentRepository, never()).save(any());
    }
}
