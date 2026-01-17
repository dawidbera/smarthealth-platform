package io.smartcare.platform.patient.controller;

import io.smartcare.platform.patient.domain.Patient;
import io.smartcare.platform.patient.dto.CreatePatientRequest;
import io.smartcare.platform.patient.repository.PatientRepository;
import io.smartcare.platform.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientRepository patientRepository;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody CreatePatientRequest request) {
        Patient patient = Patient.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .nationalId(request.nationalId())
                .build();

        Patient savedPatient = patientService.registerPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    @GetMapping("/{id}/exists")
    public boolean exists(@PathVariable Long id) {
        return patientRepository.existsById(id);
    }
}
