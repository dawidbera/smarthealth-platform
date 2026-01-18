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

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private PatientRepository patientRepository;

    @Test
    void getAllPatients_ShouldReturnList() throws Exception {
        Patient patient = Patient.builder().id(1L).firstName("Jan").build();
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        mockMvc.perform(get("/patient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jan"));
    }

    @Test
    void createPatient_ShouldReturnCreated() throws Exception {
        Patient patient = Patient.builder().id(1L).firstName("Jan").build();
        when(patientService.registerPatient(any(Patient.class))).thenReturn(patient);

        String json = "{\"firstName\":\"Jan\", \"lastName\":\"Kowalski\", \"email\":\"jan@example.com\", \"nationalId\":\"123\"}";

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jan"));
    }

    @Test
    void exists_ShouldReturnTrue() throws Exception {
        when(patientRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/patient/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
