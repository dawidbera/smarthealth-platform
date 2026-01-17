package io.smartcare.platform.notification.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PatientEventListener {

    @RabbitListener(queues = "q.patient.created")
    public void handlePatientCreated(String message) {
        log.info("NOTIFICATION RECEIVED: Received message from RabbitMQ: {}", message);
        log.info("Sending welcome notification to the patient...");
    }
}
