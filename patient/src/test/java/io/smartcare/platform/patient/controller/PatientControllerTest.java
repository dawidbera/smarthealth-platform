package io.smartcare.platform.patient.controller;

import io.smartcare.platform.patient.domain.Patient;
import io.smartcare.platform.patient.repository.PatientRepository;
import io.smartcare.platform.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the Patient Controller.
 * Tests CRUD operations and patient existence checks via REST endpoints.
 * Uses MockMvc to test the API in isolation without loading the full application context.
 */
@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private PatientRepository patientRepository;

    /**
     * Tests that the GET /patient endpoint returns a list of all patients.
     * Verifies that the response status is 200 OK and contains patient data.
     */
    @Test
    void getAllPatients_ShouldReturnList() throws Exception {
        Patient patient = Patient.builder().id(1L).firstName("John").build();
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        mockMvc.perform(get("/patient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    /**
     * Tests that the POST /patient endpoint successfully creates a new patient.
     * Verifies that the response status is 201 Created and contains the created patient data.
     */
    @Test
    void createPatient_ShouldReturnCreated() throws Exception {
        Patient patient = Patient.builder().id(1L).firstName("John").build();
        when(patientService.registerPatient(any(Patient.class))).thenReturn(patient);

        String json = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"nationalId\":\"123\"}";

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    /**
     * Tests that the GET /patient/{id}/exists endpoint returns true when a patient exists.
     * Verifies that the response is 200 OK with a boolean value indicating existence.
     */
    @Test
    void exists_ShouldReturnTrue() throws Exception {
        when(patientRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/patient/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}