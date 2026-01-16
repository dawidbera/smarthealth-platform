package io.smartcare.platform.patient.service;

import io.smartcare.platform.patient.domain.Patient;
import io.smartcare.platform.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Patient registerPatient(Patient patient) {
        log.info("Registering patient: {} {}", patient.getFirstName(), patient.getLastName());
        
        if (patientRepository.findByPesel(patient.getPesel()).isPresent()) {
            throw new IllegalArgumentException("Patient with PESEL " + patient.getPesel() + " already exists");
        }

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient saved with ID: {}", savedPatient.getId());
        
        rabbitTemplate.convertAndSend("patient.created", "Patient created with ID: " + savedPatient.getId());
        
        return savedPatient;
    }
}
