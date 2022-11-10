package ru.netology.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MainTest {

    PatientInfoFileRepository patientInfoFileRepository = mock(PatientInfoFileRepository.class);
    SendAlertServiceImpl sendAlertService = Mockito.spy(SendAlertServiceImpl.class);

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

    @Test
    void checkBloodPressureNoMessage() {
        String patientId = "c3388c3e-df06-459c-b7db-fdb8a73a2fb7";
        BloodPressure bloodPressure = new BloodPressure(120, 80);

        PatientInfo patientInfo = new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));

        Mockito.when(patientInfoFileRepository.getById(Mockito.anyString()))
                .thenReturn(patientInfo);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkBloodPressure(patientId, bloodPressure);
    }

    @Test
    void checkTemperatureNoMessage() {
        String patientId = "58c7e023-0704-4f49-93a0-943a6fa833cc";
        BigDecimal temp = new BigDecimal("36.6");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule(), new ParameterNamesModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        File repoFile = new File("patients.txt");

        patientInfoFileRepository = new PatientInfoFileRepository(repoFile, mapper);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkTemperature(patientId, temp);
        Mockito.verify(sendAlertService, Mockito.times(0)).send("Warning, patient with id: 5718a1e7-3728-4cfb-8d83-0a45452c4961, need help");

    }

    @Test
    void checkTemperatureMessage() {
        String patientId = "58c7e023-0704-4f49-93a0-943a6fa833cc";
        BigDecimal temp = new BigDecimal("33.5");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule(), new ParameterNamesModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        File repoFile = new File("patients.txt");

        patientInfoFileRepository = new PatientInfoFileRepository(repoFile, mapper);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkTemperature(patientId, temp);
        Mockito.verify(sendAlertService, Mockito.times(1)).send("Warning, patient with id: 58c7e023-0704-4f49-93a0-943a6fa833cc, need help");

    }
}