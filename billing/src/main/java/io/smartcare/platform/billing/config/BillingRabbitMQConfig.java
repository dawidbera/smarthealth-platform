package io.smartcare.platform.billing.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingRabbitMQConfig {

    public static final String EXCHANGE = "internal.exchange";
    public static final String QUEUE_BILLING = "q.appointment.booked.billing";
    public static final String ROUTING_KEY = "appointment.booked";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue billingQueue() {
        return new Queue(QUEUE_BILLING);
    }

    @Bean
    public Binding billingBinding() {
        return BindingBuilder.bind(billingQueue()).to(internalExchange()).with(ROUTING_KEY);
    }
}
