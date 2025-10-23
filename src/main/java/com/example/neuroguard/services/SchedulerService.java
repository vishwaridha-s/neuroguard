package com.example.neuroguard.services;

import com.example.neuroguard.model.Alert;
import com.example.neuroguard.model.Caregiver;
import com.example.neuroguard.model.Patient;
import com.example.neuroguard.model.Vitals;
import com.example.neuroguard.repositories.AlertRepository;
import com.example.neuroguard.repositories.CaregiverRepository;
import com.example.neuroguard.repositories.PatientRepository;
import com.example.neuroguard.repositories.VitalsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final VitalsRepository vitalsRepo;
    private final PatientRepository patientRepo;
    private final CaregiverRepository caregiverRepo;
    private final AlertRepository alertRepo;
    private final MLService mlService;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 600000) // every 10 min
    public void checkVitals() {
        List<Vitals> unpredictedVitals = vitalsRepo.findByMlPredictionIsNull();
        for (Vitals vitals : unpredictedVitals) {
            String prediction = mlService.predict(vitals);
            vitals.setMlPrediction(prediction);

            if (!prediction.equalsIgnoreCase("normal")) {
                vitals.setAlerted(true);

                Patient patient = patientRepo.findById(vitals.getPatientId()).orElse(null);
                if (patient == null) continue;
                Caregiver caregiver = caregiverRepo.findById(patient.getCaregiverId()).orElse(null);
                if (caregiver == null) continue;

                Alert alert = Alert.builder()
                        .patientId(patient.getId())
                        .caregiverId(caregiver.getId())
                        .timestamp(LocalDateTime.now())
                        .type(prediction)
                        .heartRate(vitals.getHeartRate())
                        .spo2(vitals.getSpo2())
                        .temperature(vitals.getTemperature())
                        .latitude(vitals.getLatitude())
                        .longitude(vitals.getLongitude())
                        .message("NeuroGuard Alert: " + prediction)
                        .build();

                alertRepo.save(alert);
                notificationService.sendAlert(patient, caregiver, alert);
            }
            vitalsRepo.save(vitals);
        }
    }
}
