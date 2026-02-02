package io.smartcare.platform.billing.listener;

import io.smartcare.platform.billing.domain.Invoice;
import io.smartcare.platform.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the Appointment Event Listener in the Billing Service.
 * Tests that appointment booking messages are correctly processed and invoices are created.
 * Uses Mockito to mock the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentEventListenerTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private AppointmentEventListener appointmentEventListener;

    /**
     * Tests that when an appointment booked message is received,
     * a new invoice is created with the correct appointment ID, amount, and status.
     * Verifies the invoice is saved to the repository using ArgumentCaptor.
     */
    @Test
    void shouldCreateInvoiceWhenAppointmentBookedMessageReceived() {
        // Given
        String message = "Appointment booked with ID: 123";

        // When
        appointmentEventListener.handleAppointmentBooked(message);

        // Then
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());

        Invoice savedInvoice = invoiceCaptor.getValue();
        assertEquals(123L, savedInvoice.getAppointmentId());
        assertEquals(new BigDecimal("150.00"), savedInvoice.getAmount());
        assertEquals("PENDING", savedInvoice.getStatus());
    }
}
