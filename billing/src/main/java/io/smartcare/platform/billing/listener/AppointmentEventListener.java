package io.smartcare.platform.billing.listener;

import io.smartcare.platform.billing.config.BillingRabbitMQConfig;
import io.smartcare.platform.billing.domain.Invoice;
import io.smartcare.platform.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final InvoiceRepository invoiceRepository;

    @RabbitListener(queues = BillingRabbitMQConfig.QUEUE_BILLING)
    public void handleAppointmentBooked(String message) {
        log.info("BILLING RECEIVED: Received message: {}", message);
        
        // Simulating parsing appointment ID from message "Appointment booked with ID: 1"
        try {
            String idStr = message.substring(message.lastIndexOf(" ") + 1);
            Long appointmentId = Long.parseLong(idStr);

            Invoice invoice = Invoice.builder()
                    .appointmentId(appointmentId)
                    .amount(new BigDecimal("150.00")) // Fixed price for now
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            invoiceRepository.save(invoice);
            log.info("Invoice created for appointment ID: {}", appointmentId);
            
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }
}
