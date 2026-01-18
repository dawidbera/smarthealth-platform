package io.smartcare.platform.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitMQConfig {

    public static final String EXCHANGE = "internal.exchange";
    public static final String QUEUE_APPOINTMENT_NOTIFICATION = "q.appointment.booked.notification";
    public static final String ROUTING_KEY_APPOINTMENT = "appointment.booked";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue patientCreatedQueue() {
        return new Queue("q.patient.created");
    }

    @Bean
    public Queue appointmentNotificationQueue() {
        return new Queue(QUEUE_APPOINTMENT_NOTIFICATION);
    }

    @Bean
    public Binding bindingAppointmentNotification() {
        return BindingBuilder
                .bind(appointmentNotificationQueue())
                .to(internalExchange())
                .with(ROUTING_KEY_APPOINTMENT);
    }
}
