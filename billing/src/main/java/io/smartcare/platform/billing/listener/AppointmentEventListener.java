package io.smartcare.platform.billing.listener;

import io.smartcare.platform.billing.config.BillingRabbitMQConfig;
import io.smartcare.platform.billing.domain.Invoice;
import io.smartcare.platform.billing.repository.InvoiceRepository;
import io.smartcare.platform.billing.service.S3Service;
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
    private final S3Service s3Service;

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

            // Generate and upload invoice document
            String invoiceContent = generateInvoiceContent(invoice);
            s3Service.uploadInvoice(appointmentId, invoiceContent);
            
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }

    private String generateInvoiceContent(Invoice invoice) {
        return "INVOICE\n" +
               "-------\n" +
               "Appointment ID: " + invoice.getAppointmentId() + "\n" +
               "Date: " + invoice.getCreatedAt() + "\n" +
               "Amount: " + invoice.getAmount() + " USD\n" +
               "Status: " + invoice.getStatus() + "\n";
    }
}
