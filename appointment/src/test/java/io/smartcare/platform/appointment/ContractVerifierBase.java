package io.smartcare.platform.appointment;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.integration.channel.QueueChannel;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
@DirtiesContext
@Import(ContractVerifierBase.TestConfig.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public abstract class ContractVerifierBase {

    @Autowired
    private AppointmentService appointmentService;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("internal.exchange")
    private MessageChannel internalExchange;

    @TestConfiguration
    public static class TestConfig {
        @Bean("internal.exchange")
        public MessageChannel internalExchange() {
            return new QueueChannel();
        }
    }

    @BeforeEach
    public void setup() {
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        // Mock WebClient for patient check
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        // Bridge RabbitTemplate to Spring Integration channel for Contract Verifier
        doAnswer(invocation -> {
            String routingKey = invocation.getArgument(1);
            Object payload = invocation.getArgument(2);
            internalExchange.send(MessageBuilder.withPayload(payload)
                    .setHeader("amqp_routingKey", routingKey)
                    .build());
            return null;
        }).when(rabbitTemplate).convertAndSend(eq("internal.exchange"), anyString(), anyString());
    }

    public void triggerBookedEvent() {
        Appointment appointment = Appointment.builder()
                .patientId(1L)
                .doctorId(101L)
                .appointmentTime(LocalDateTime.now())
                .status(AppointmentStatus.REQUESTED)
                .build();
        
        appointmentService.bookAppointment(appointment);
    }
}
