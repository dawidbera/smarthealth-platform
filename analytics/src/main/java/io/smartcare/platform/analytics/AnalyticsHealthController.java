package io.smartcare.platform.analytics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsHealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "analytics", "status", "ok");
    }
}
