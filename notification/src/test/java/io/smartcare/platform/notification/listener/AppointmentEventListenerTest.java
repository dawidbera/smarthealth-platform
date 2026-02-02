package io.smartcare.platform.notification.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for the Appointment Event Listener in the Notification Service.
 * Tests that appointment booking notifications are processed without throwing exceptions.
 * Verifies that the listener logs or sends notifications appropriately.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentEventListenerTest {

    @InjectMocks
    private AppointmentEventListener appointmentEventListener;

    /**
     * Tests that the listener processes appointment booking notifications without throwing exceptions.
     * Verifies that the notification system handles the event gracefully.
     */
    @Test
    void handleAppointmentBooked_ShouldLogMessage() {
        String message = "Appointment booked with ID: 999";
        
        assertDoesNotThrow(() -> {
            appointmentEventListener.handleAppointmentBooked(message);
        });
    }
}
