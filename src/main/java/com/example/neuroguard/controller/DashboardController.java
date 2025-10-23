package com.example.neuroguard.controller;
import com.example.neuroguard.model.Alert;
import com.example.neuroguard.repositories.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AlertRepository alertRepo;
    @GetMapping("/patient/{patientId}/summary")
    public ResponseEntity<?> getPatientSummary(@PathVariable String patientId) {
        List<Alert> alerts = alertRepo.findByPatientIdOrderByTimestampDesc(patientId);
        return ResponseEntity.ok(alerts);
    }
}
