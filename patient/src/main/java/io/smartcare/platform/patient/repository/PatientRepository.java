package io.smartcare.platform.patient.repository;

import io.smartcare.platform.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPesel(String pesel);
    Optional<Patient> findByEmail(String email);
}
