package com.example.neuroguard.services;

import com.example.neuroguard.model.Caregiver;
import com.example.neuroguard.model.Patient;
import com.example.neuroguard.repositories.CaregiverRepository;
import com.example.neuroguard.repositories.PatientRepository;
import com.example.neuroguard.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PatientRepository patientRepo;
    private final CaregiverRepository caregiverRepo;

    // Register patient
    public Patient registerPatient(Patient patient) {
        String code = CodeGenerator.generateUniqueCode();
        patient.setCode(code);
        patient.setCreatedAt(LocalDateTime.now());
        return patientRepo.save(patient);
    }

    public Patient getPatientById(String patientId) {
        return patientRepo.findById(patientId).orElse(null);
    }

    // Register caregiver
    public Caregiver registerCaregiver(Caregiver caregiver) {
        caregiver.setPatientIds(null);
        return caregiverRepo.save(caregiver);
    }

    public Caregiver getCaregiverById(String caregiverId) {
        return caregiverRepo.findById(caregiverId).orElse(null);
    }

    // Login patient
    public Optional<Patient> loginPatient(String email) {
        return patientRepo.findByEmail(email);
    }

    // Login caregiver
    public Optional<Caregiver> loginCaregiver(String email) {
        return caregiverRepo.findByEmail(email);
    }

    // Link patient to caregiver
    public boolean linkPatientToCaregiver(String caregiverId, String code) {
        Optional<Patient> patientOpt = patientRepo.findByCode(code);
        Optional<Caregiver> caregiverOpt = caregiverRepo.findById(caregiverId);

        if (patientOpt.isPresent() && caregiverOpt.isPresent()) {
            Patient patient = patientOpt.get();
            Caregiver caregiver = caregiverOpt.get();

            patient.setCaregiverId(caregiver.getId());
            patientRepo.save(patient);

            if (caregiver.getPatientIds() != null) {
                caregiver.getPatientIds().add(patient.getId());
            } else {
                caregiver.setPatientIds(new java.util.ArrayList<>());
                caregiver.getPatientIds().add(patient.getId());
            }
            caregiverRepo.save(caregiver);
            return true;
        }
        return false;
    }
}
