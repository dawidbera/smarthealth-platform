package io.smartcare.platform.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationHealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "notification", "status", "ok");
    }
}
