package io.smartcare.platform.billing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/billing")
public class BillingHealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "billing", "status", "ok");
    }
}
