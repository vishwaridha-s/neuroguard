package com.example.neuroguard.services;

import com.example.neuroguard.model.Alert;
import com.example.neuroguard.model.Patient;
import com.example.neuroguard.model.Vitals;
import com.example.neuroguard.repositories.VitalsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VitalsService {

    private final VitalsRepository vitalsRepo;
    private final AuthService authService;
    private final NotificationService notificationService;

    public Vitals saveVitals(Vitals vitals) {
        if (vitals.getTimestamp() == null) {
            vitals.setTimestamp(LocalDateTime.now());
        }
        return vitalsRepo.save(vitals);
    }

    public List<Vitals> getVitalsForPatient(String patientId) {
        return vitalsRepo.findByPatientIdOrderByTimestampDesc(patientId);
    }

    public void processAlert(Vitals vitals, String prediction) {
        if ("seizure".equalsIgnoreCase(prediction) || "panic".equalsIgnoreCase(prediction)) {
            vitals.setAlerted(true);

            Patient patient = authService.getPatientById(vitals.getPatientId());
            if (patient != null) {
                Alert alert = Alert.builder()
                        .patientId(patient.getId())
                        .heartRate(vitals.getHeartRate())
                        .spo2(vitals.getSpo2())
                        .temperature(vitals.getTemperature())
                        .latitude(vitals.getLatitude())
                        .longitude(vitals.getLongitude())
                        .type(prediction)
                        .timestamp(vitals.getTimestamp())
                        .message("⚠️ NeuroGuard Alert: Possible " + prediction.toUpperCase() + " detected!\n\n" +
                                "Heart Rate: " + vitals.getHeartRate() + " bpm\n" +
                                "SpO₂: " + vitals.getSpo2() + "%\n" +
                                "Temperature: " + vitals.getTemperature() + "°C\n" +
                                "Location: https://maps.google.com/?q=" + vitals.getLatitude() + "," + vitals.getLongitude())
                        .build();

                // Send WhatsApp alert to fixed number
                notificationService.sendWhatsAppAlert(alert);
            }
        } else {
            vitals.setAlerted(false);
        }
    }
}
