package io.smartcare.platform.appointment.controller;

import io.smartcare.platform.appointment.domain.Appointment;
import io.smartcare.platform.appointment.domain.AppointmentStatus;
import io.smartcare.platform.appointment.repository.AppointmentRepository;
import io.smartcare.platform.appointment.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the Appointment Controller.
 * Tests CRUD operations and appointment booking via REST endpoints.
 * Uses MockMvc to test the API without loading the full application context.
 */
@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private AppointmentRepository appointmentRepository;

    /**
     * Tests that the GET /appointment endpoint returns a list of all appointments.
     * Verifies that the response status is 200 OK and contains appointment data.
     */
    @Test
    void getAllAppointments_ShouldReturnList() throws Exception {
        Appointment app = Appointment.builder().id(1L).patientId(1L).doctorId(101L).build();
        when(appointmentRepository.findAll()).thenReturn(List.of(app));

        mockMvc.perform(get("/appointment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorId").value(101));
    }

    /**
     * Tests that the POST /appointment endpoint successfully books a new appointment.
     * Verifies that the response status is 201 Created and contains the booked appointment status.
     */
    @Test
    void bookAppointment_ShouldReturnCreated() throws Exception {
        Appointment app = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(101L)
                .status(AppointmentStatus.BOOKED)
                .build();
        
        when(appointmentService.bookAppointment(any(Appointment.class))).thenReturn(app);

        String json = "{\"patientId\":1, \"doctorId\":101, \"appointmentTime\":\"2026-02-18T10:00:00\"}";

        mockMvc.perform(post("/appointment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }
}
