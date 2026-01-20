package io.smartcare.platform.notification.listener;

import io.smartcare.platform.notification.config.NotificationRabbitMQConfig;
import io.smartcare.platform.notification.dto.MedicalAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MedicalAlertEventListener {

    @RabbitListener(queues = NotificationRabbitMQConfig.QUEUE_MEDICAL_ALERTS)
    public void handleMedicalAlert(MedicalAlert alert) {
        log.error("🛑 MEDICAL ALERT RECEIVED for patient {}: {}", alert.patientId(), alert.message());
        log.error("Details: Type={}, Value={}, Time={}", alert.type(), alert.lastValue(), alert.timestamp());
        
        // Tutaj w przyszłości mogłaby być wysyłka SMS/Email
        log.info("Simulating SMS notification to doctor...");
    }
}
