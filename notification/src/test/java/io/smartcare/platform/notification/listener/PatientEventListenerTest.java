package io.smartcare.platform.notification.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class PatientEventListenerTest {

    @InjectMocks
    private PatientEventListener patientEventListener;

    @Test
    void handlePatientCreated_ShouldLogMessage() {
        String message = "Patient created with ID: 1";
        
        // This is a simple test to ensure no exceptions are thrown during processing
        assertDoesNotThrow(() -> {
            patientEventListener.handlePatientCreated(message);
        });
    }
}
