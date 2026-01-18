package io.smartcare.platform.notification.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class AppointmentEventListenerTest {

    @InjectMocks
    private AppointmentEventListener appointmentEventListener;

    @Test
    void handleAppointmentBooked_ShouldLogMessage() {
        String message = "Appointment booked with ID: 999";
        
        assertDoesNotThrow(() -> {
            appointmentEventListener.handleAppointmentBooked(message);
        });
    }
}
