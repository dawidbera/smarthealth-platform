package io.smartcare.platform.patient;

import io.smartcare.platform.patient.controller.PatientController;
import io.smartcare.platform.patient.repository.PatientRepository;
import io.smartcare.platform.patient.service.PatientService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

@SpringBootTest
public abstract class ContractVerifierBase {

    @Autowired
    private PatientController patientController;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private PatientService patientService;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(patientController);
        when(patientRepository.existsById(1L)).thenReturn(true);
    }
}
