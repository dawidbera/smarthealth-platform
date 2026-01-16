package io.smartcare.platform.device;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/device")
public class DeviceHealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "device", "status", "ok");
    }
}
