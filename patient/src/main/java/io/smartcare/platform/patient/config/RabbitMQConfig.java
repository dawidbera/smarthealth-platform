package io.smartcare.platform.patient.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "internal.exchange";
    public static final String QUEUE_PATIENT_CREATED = "q.patient.created";
    public static final String ROUTING_KEY_PATIENT_CREATED = "patient.created";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue patientCreatedQueue() {
        return new Queue(QUEUE_PATIENT_CREATED);
    }

    @Bean
    public Binding bindingPatientCreated() {
        return BindingBuilder
                .bind(patientCreatedQueue())
                .to(internalExchange())
                .with(ROUTING_KEY_PATIENT_CREATED);
    }
}
