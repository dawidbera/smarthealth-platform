package io.smartcare.platform.appointment;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class ContractVerifierBase {

    @Autowired
    private AppointmentService appointmentService;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @MockBean
    private WebClient webClient;

    @MockBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @MockBean
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @MockBean
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    public void setup() {
        // Mock WebClient for patient check
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
    }

    public void triggerBookedEvent() {
        Appointment appointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(101L)
                .appointmentTime(LocalDateTime.now())
                .status(AppointmentStatus.REQUESTED)
                .build();
        
        appointmentService.bookAppointment(appointment);
    }
}
