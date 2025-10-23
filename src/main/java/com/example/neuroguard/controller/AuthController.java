package com.example.neuroguard.controller;

import com.example.neuroguard.model.Caregiver;
import com.example.neuroguard.model.Patient;
import com.example.neuroguard.services.AuthService;
import com.example.neuroguard.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

// DTO for login
record LoginRequest(String email) {}

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final NotificationService notificationService;

    // ✅ Patient signup
    @PostMapping("/signup/patient")
    public ResponseEntity<?> signupPatient(@RequestBody Patient patient) {
        try {
            Patient saved = authService.registerPatient(patient);
            notificationService.sendPatientCode(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Caregiver signup
    @PostMapping("/signup/caregiver")
    public ResponseEntity<?> signupCaregiver(@RequestBody Caregiver caregiver) {
        try {
            Caregiver saved = authService.registerCaregiver(caregiver);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Patient login
    @PostMapping("/login/patient")
    public ResponseEntity<?> loginPatient(@RequestBody LoginRequest request) {
        if (request.email() == null || request.email().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        Optional<Patient> patient = authService.loginPatient(request.email());
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Patient not found"));
        }
    }

    // ✅ Caregiver login
    @PostMapping("/login/caregiver")
    public ResponseEntity<?> loginCaregiver(@RequestBody LoginRequest request) {
        if (request.email() == null || request.email().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        Optional<Caregiver> caregiver = authService.loginCaregiver(request.email());
        if (caregiver.isPresent()) {
            return ResponseEntity.ok(caregiver.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Caregiver not found"));
        }
    }

    // ✅ Caregiver links patient using unique code
    @PostMapping("/caregiver/link")
    public ResponseEntity<?> linkPatient(@RequestParam String caregiverId,
                                         @RequestParam String code) {
        boolean success = authService.linkPatientToCaregiver(caregiverId, code);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Patient linked successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid code or caregiverId"));
        }
    }
}
