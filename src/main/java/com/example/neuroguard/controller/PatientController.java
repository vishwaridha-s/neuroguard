package com.example.neuroguard.controller;

import com.example.neuroguard.model.Alert;
import com.example.neuroguard.model.Patient;
import com.example.neuroguard.model.Vitals;
import com.example.neuroguard.repositories.AlertRepository;
import com.example.neuroguard.repositories.PatientRepository;
import com.example.neuroguard.services.VitalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository patientRepo;
    private final VitalsService vitalsService;
    private final AlertRepository alertRepo;

    // Get patient details by ID
    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatient(@PathVariable String patientId) {
        Optional<Patient> patientOpt = patientRepo.findById(patientId);
        if (patientOpt.isEmpty()) return ResponseEntity.badRequest().body("Patient not found");
        return ResponseEntity.ok(patientOpt.get());
    }

    // Get all vitals of a patient
    @GetMapping("/{patientId}/vitals")
    public ResponseEntity<?> getPatientVitals(@PathVariable String patientId) {
        List<Vitals> vitals = vitalsService.getVitalsForPatient(patientId);
        return ResponseEntity.ok(vitals);
    }

    // Get latest vitals
    @GetMapping("/{patientId}/vitals/latest")
    public ResponseEntity<?> getLatestVitals(@PathVariable String patientId) {
        List<Vitals> vitals = vitalsService.getVitalsForPatient(patientId);
        if (vitals.isEmpty()) return ResponseEntity.ok("No vitals found");
        return ResponseEntity.ok(vitals.get(0));
    }

    // Get all alerts for a patient
    @GetMapping("/{patientId}/alerts")
    public ResponseEntity<?> getPatientAlerts(@PathVariable String patientId) {
        List<Alert> alerts = alertRepo.findByPatientIdOrderByTimestampDesc(patientId);
        return ResponseEntity.ok(alerts);
    }

    // Get latest alert
    @GetMapping("/{patientId}/alerts/latest")
    public ResponseEntity<?> getLatestAlert(@PathVariable String patientId) {
        List<Alert> alerts = alertRepo.findByPatientIdOrderByTimestampDesc(patientId);
        if (alerts.isEmpty()) return ResponseEntity.ok("No alerts found");
        return ResponseEntity.ok(alerts.get(0));
    }

    // Get patient summary (details + latest vitals + latest alert)
    @GetMapping("/{patientId}/summary")
    public ResponseEntity<?> getPatientSummary(@PathVariable String patientId) {
        Optional<Patient> patientOpt = patientRepo.findById(patientId);
        if (patientOpt.isEmpty()) return ResponseEntity.badRequest().body("Patient not found");

        Patient patient = patientOpt.get();
        List<Vitals> vitals = vitalsService.getVitalsForPatient(patientId);
        List<Alert> alerts = alertRepo.findByPatientIdOrderByTimestampDesc(patientId);

        return ResponseEntity.ok(new Object() {
            public final Patient patientDetails = patient;
            public final Vitals latestVitals = vitals.isEmpty() ? null : vitals.get(0);
            public final Alert latestAlert = alerts.isEmpty() ? null : alerts.get(0);
            public final List<Vitals> allVitals = vitals;
            public final List<Alert> allAlerts = alerts;
        });
    }
}
