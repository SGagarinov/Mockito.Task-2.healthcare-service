package ru.netology.patient.service.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SendAlertServiceImplTest {

    SendAlertServiceImpl sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
    PatientInfoFileRepository patientInfoFileRepository = mock(PatientInfoFileRepository.class);

    @Test
    void checkBloodPressureMessage() {
        String patientId = "4a80c2d6-41c8-49af-b1ab-05be2889baf7";
        BloodPressure bloodPressure = new BloodPressure(160, 100);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule(), new ParameterNamesModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        File repoFile = new File("patients.txt");

        patientInfoFileRepository = new PatientInfoFileRepository(repoFile, mapper);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkBloodPressure(patientId, bloodPressure);

        Mockito.verify(sendAlertService, Mockito.times(1)).send("Warning, patient with id: 4a80c2d6-41c8-49af-b1ab-05be2889baf7, need help");
    }
}