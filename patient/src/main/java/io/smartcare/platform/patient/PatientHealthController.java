package io.smartcare.platform.patient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientHealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "patient", "status", "ok");
    }
}
