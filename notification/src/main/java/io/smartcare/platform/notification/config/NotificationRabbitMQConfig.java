package io.smartcare.platform.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitMQConfig {

    @Bean
    public Queue patientCreatedQueue() {
        return new Queue("q.patient.created");
    }
}
