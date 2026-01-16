package io.smartcare.platform.patient.controller;

import io.smartcare.platform.patient.domain.Patient;
import io.smartcare.platform.patient.dto.CreatePatientRequest;
import io.smartcare.platform.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody CreatePatientRequest request) {
        Patient patient = Patient.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .pesel(request.pesel())
                .build();

        Patient savedPatient = patientService.registerPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }
}
