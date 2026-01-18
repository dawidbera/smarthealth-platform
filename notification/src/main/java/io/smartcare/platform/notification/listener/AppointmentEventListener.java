package io.smartcare.platform.notification.listener;

import io.smartcare.platform.notification.config.NotificationRabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppointmentEventListener {

    @RabbitListener(queues = NotificationRabbitMQConfig.QUEUE_APPOINTMENT_NOTIFICATION)
    public void handleAppointmentBooked(String message) {
        log.info("NOTIFICATION RECEIVED: Received message from RabbitMQ: {}", message);
        log.info("Sending confirmation email to patient...");
    }
}
