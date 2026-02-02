package io.smartcare.platform.analytics.controller;

import io.smartcare.platform.analytics.domain.TelemetryRecord;
import io.smartcare.platform.analytics.repository.TelemetryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the Analytics Controller.
 * Tests device statistics endpoints including calculations for average, max, and anomaly detection.
 * Uses MockMvc to test REST endpoints without loading the full Spring context.
 */
@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelemetryRepository telemetryRepository;

    /**
     * Tests that device statistics are correctly calculated when telemetry records exist.
     */
    @Test
    void getDeviceStats_ShouldCalculateCorrectly() throws Exception {
        TelemetryRecord r1 = TelemetryRecord.builder().value(80.0).build();
        TelemetryRecord r2 = TelemetryRecord.builder().value(100.0).build();
        
        when(telemetryRepository.findBySerialNumber(eq("SN-1"), any(PageRequest.class)))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/analytics/device/SN-1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.average").value(90.0))
                .andExpect(jsonPath("$.max").value(100.0))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.status").value("Normal"));
    }

    /**
     * Tests that device statistics are handled correctly when no telemetry records are found.
     */
    /**
     * Tests that device statistics are handled correctly when no telemetry records are found.
     */
    @Test
    void getDeviceStats_ShouldHandleEmptyData() throws Exception {
        when(telemetryRepository.findBySerialNumber(eq("SN-EMPTY"), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/analytics/device/SN-EMPTY/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.status").value("No data"));
    }
}
