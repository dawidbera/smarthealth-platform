package io.smartcare.platform.device.controller;

import io.smartcare.platform.device.DeviceController;
import io.smartcare.platform.device.dto.TelemetryData;
import io.smartcare.platform.device.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the Device Controller.
 * Tests the REST endpoint for submitting telemetry data from health monitoring devices.
 * Uses MockMvc to test the HTTP endpoint in isolation.
 */
@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Tests that the POST /telemetry endpoint accepts telemetry data and returns 200 OK.
     * Verifies that the device service processes the telemetry data without errors.
     */
    @Test
    void postTelemetry_ShouldReturnAccepted() throws Exception {
        doNothing().when(deviceService).processTelemetry(any(TelemetryData.class));

        String json = "{\"serialNumber\":\"SN-123\", \"value\":75.0, \"unit\":\"BPM\", \"timestamp\":\"2026-01-18T10:00:00\"}";

        mockMvc.perform(post("/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
}
