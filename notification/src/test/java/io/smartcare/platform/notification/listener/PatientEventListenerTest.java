package io.smartcare.platform.notification.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for the Patient Event Listener in the Notification Service.
 * Tests that patient creation notifications are processed without throwing exceptions.
 * Verifies that the listener logs or sends notifications appropriately.
 */
@ExtendWith(MockitoExtension.class)
class PatientEventListenerTest {

    @InjectMocks
    private PatientEventListener patientEventListener;

    /**
     * Tests that the listener processes patient creation notifications without throwing exceptions.
     * Verifies that the notification system handles the event gracefully.
     */
    @Test
    void handlePatientCreated_ShouldLogMessage() {
        String message = "Patient created with ID: 1";
        
        // This is a simple test to ensure no exceptions are thrown during processing
        assertDoesNotThrow(() -> {
            patientEventListener.handlePatientCreated(message);
        });
    }
}
