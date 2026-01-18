package io.smartcare.platform.billing.integration;

import io.smartcare.platform.billing.config.BillingRabbitMQConfig;
import io.smartcare.platform.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class BillingIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    void shouldCreateInvoiceWhenAppointmentBookedEventReceived() {
        // Given
        String message = "Appointment booked with ID: 555";

        // When: Send message to the exchange
        rabbitTemplate.convertAndSend(BillingRabbitMQConfig.EXCHANGE, BillingRabbitMQConfig.ROUTING_KEY, message);

        // Then: Wait for the listener to process and save to DB
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(invoiceRepository.findAll())
                    .extracting("appointmentId")
                    .contains(555L);
        });
    }
}
