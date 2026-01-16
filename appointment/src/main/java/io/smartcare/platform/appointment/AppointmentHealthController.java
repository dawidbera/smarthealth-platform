package io.smartcare.platform.appointment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class AppointmentHealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "appointment", "status", "ok");
    }
}
