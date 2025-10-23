package com.example.neuroguard.controller;

import com.example.neuroguard.model.Vitals;
import com.example.neuroguard.services.MLService;
import com.example.neuroguard.services.VitalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/vitals")
@RequiredArgsConstructor
public class VitalsController {

    private final VitalsService vitalsService;
    private final MLService mlService;

    // Only one patient monitored at a time
    private final Map<String, Vitals> monitorMap = new ConcurrentHashMap<>();

    // 1️⃣ Frontend initializes monitor: patientId + location
    @PostMapping("/monitor/init")
    public ResponseEntity<?> initMonitor(@RequestBody Map<String, Object> payload) {
        String patientId = (String) payload.get("patientId");
        Double latitude = (Double) payload.get("latitude");
        Double longitude = (Double) payload.get("longitude");

        if(patientId == null || latitude == null || longitude == null){
            return ResponseEntity.badRequest().body(Map.of("error", "Missing patientId or location"));
        }

        Vitals vitals = new Vitals();
        vitals.setPatientId(patientId);
        vitals.setLatitude(latitude);
        vitals.setLongitude(longitude);

        monitorMap.put(patientId, vitals);
        return ResponseEntity.ok(Map.of("message", "Monitor initialized for patient " + patientId));
    }

    // 2️⃣ ESP32 sends vitals only
    @PostMapping("/monitor/hardware")
    public ResponseEntity<?> receiveHardwareVitals(@RequestBody Vitals hardwareVitals) {
        if (monitorMap.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No monitored patient. Frontend must init monitor first."));
        }

        String patientId = monitorMap.keySet().iterator().next();
        Vitals mergedVitals = monitorMap.get(patientId);

        // Merge hardware vitals
        mergedVitals.setHeartRate(hardwareVitals.getHeartRate());
        mergedVitals.setSpo2(hardwareVitals.getSpo2());
        mergedVitals.setAccelX(hardwareVitals.getAccelX());
        mergedVitals.setAccelY(hardwareVitals.getAccelY());
        mergedVitals.setAccelZ(hardwareVitals.getAccelZ());
        mergedVitals.setGyroX(hardwareVitals.getGyroX());
        mergedVitals.setGyroY(hardwareVitals.getGyroY());
        mergedVitals.setGyroZ(hardwareVitals.getGyroZ());
        mergedVitals.setTemperature(hardwareVitals.getTemperature());

        // Save, predict, alert
        Vitals savedVitals = vitalsService.saveVitals(mergedVitals);
        String prediction = mlService.predict(savedVitals);
        savedVitals.setMlPrediction(prediction);
        vitalsService.processAlert(savedVitals, prediction);
        savedVitals = vitalsService.saveVitals(savedVitals);

        return ResponseEntity.ok(Map.of(
                "vitals", savedVitals,
                "prediction", prediction
        ));
    }

    // 3️⃣ Manual upload for testing buttons
    @PostMapping("/upload")
    public ResponseEntity<?> uploadVitals(@RequestBody Vitals vitals) {
        String prediction = mlService.predict(vitals);
        vitals.setMlPrediction(prediction);
        vitalsService.processAlert(vitals, prediction);
        Vitals saved = vitalsService.saveVitals(vitals);

        return ResponseEntity.ok(Map.of(
                "vitals", saved,
                "prediction", prediction
        ));
    }

    // 4️⃣ Fetch latest vitals for live dashboard
    @GetMapping("/monitor/latest")
    public ResponseEntity<?> getLatestVitals() {
        if (monitorMap.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No monitored patient."));
        }
        String patientId = monitorMap.keySet().iterator().next();
        Vitals latest = monitorMap.get(patientId);
        return ResponseEntity.ok(latest);
    }
}
