package com.example.neuroguard.controller;

import com.example.neuroguard.model.Caregiver;
import com.example.neuroguard.model.Patient;
import com.example.neuroguard.model.Vitals;
import com.example.neuroguard.repositories.CaregiverRepository;
import com.example.neuroguard.repositories.PatientRepository;
import com.example.neuroguard.services.VitalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/caregiver")
@RequiredArgsConstructor
public class CaregiverController {

    private final CaregiverRepository caregiverRepo;
    private final PatientRepository patientRepo;
    private final VitalsService vitalsService;

    // Get all linked patients
    @GetMapping("/{caregiverId}/patients")
    public ResponseEntity<?> getPatients(@PathVariable String caregiverId) {
        Optional<Caregiver> caregiverOpt = caregiverRepo.findById(caregiverId);
        if (caregiverOpt.isEmpty()) return ResponseEntity.badRequest().body("Caregiver not found");

        List<String> patientIds = caregiverOpt.get().getPatientIds();
        List<Patient> patients = new ArrayList<>();
        if (patientIds != null) {
            for (String id : patientIds) {
                patientRepo.findById(id).ifPresent(patients::add);
            }
        }
        return ResponseEntity.ok(patients);
    }

    // Get a single patient's vitals history
    @GetMapping("/{caregiverId}/patient/{patientId}/vitals")
    public ResponseEntity<?> getPatientVitals(@PathVariable String caregiverId, @PathVariable String patientId) {
        Optional<Caregiver> caregiverOpt = caregiverRepo.findById(caregiverId);
        if (caregiverOpt.isEmpty()) return ResponseEntity.badRequest().body("Caregiver not found");

        if (!caregiverOpt.get().getPatientIds().contains(patientId))
            return ResponseEntity.badRequest().body("Patient not linked to caregiver");

        List<Vitals> vitals = vitalsService.getVitalsForPatient(patientId);
        return ResponseEntity.ok(vitals);
    }
}
