package io.smartcare.platform.notification.listener;

import io.smartcare.platform.notification.config.NotificationRabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class AppointmentEventListenerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private TopicExchange internalExchange;

    @Autowired
    private Queue appointmentNotificationQueue;

    @Autowired
    private Binding bindingAppointmentNotification;

    @SpyBean
    private AppointmentEventListener appointmentEventListener;

    @BeforeEach
    void setUp() {
        // Ensure infrastructure is declared in RabbitMQ before sending messages
        amqpAdmin.declareExchange(internalExchange);
        amqpAdmin.declareQueue(appointmentNotificationQueue);
        amqpAdmin.declareBinding(bindingAppointmentNotification);
    }

    @Test
    void shouldHandleAppointmentBookedEvent() {
        String message = "Appointment booked with ID: 999";
        
        rabbitTemplate.convertAndSend(NotificationRabbitMQConfig.EXCHANGE, 
                                     NotificationRabbitMQConfig.ROUTING_KEY_APPOINTMENT, 
                                     message);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            verify(appointmentEventListener).handleAppointmentBooked(message);
        });
    }
}