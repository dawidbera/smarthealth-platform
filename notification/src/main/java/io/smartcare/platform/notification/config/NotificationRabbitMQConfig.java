package io.smartcare.platform.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitMQConfig {

    public static final String EXCHANGE = "internal.exchange";
    public static final String QUEUE_APPOINTMENT_NOTIFICATION = "q.appointment.booked.notification";
    public static final String QUEUE_MEDICAL_ALERTS = "q.medical.alerts.notification";
    public static final String ROUTING_KEY_APPOINTMENT = "appointment.booked";
    public static final String ROUTING_KEY_MEDICAL_ALERT = "medical.alert";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue medicalAlertsQueue() {
        return new Queue(QUEUE_MEDICAL_ALERTS);
    }

    @Bean
    public Binding bindingMedicalAlerts() {
        return BindingBuilder
                .bind(medicalAlertsQueue())
                .to(internalExchange())
                .with(ROUTING_KEY_MEDICAL_ALERT);
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
