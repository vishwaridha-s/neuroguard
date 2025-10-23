package com.example.neuroguard.services;

import com.example.neuroguard.model.Alert;
import com.example.neuroguard.model.Caregiver;
import com.example.neuroguard.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    @Value("${twilio.to_whatsapp}")
    private String toNumber;

    @Value("${notification.to_email:}") // optional, you can set fixed email here
    private String fixedEmail;

    // Initialize Twilio once
    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
        System.out.println("Twilio initialized successfully.");
    }

    // Send patient registration code to fixed email and WhatsApp
    public void sendPatientCode(Patient patient) {
        String messageBody = "Hello " + patient.getName() + ", your NeuroGuard code is: " + patient.getCode();

        // Email - send to fixed email from properties
        if (fixedEmail != null && !fixedEmail.isEmpty()) {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(fixedEmail);
            email.setSubject("NeuroGuard Patient Registration");
            email.setText(messageBody);
            mailSender.send(email);
        }

        // WhatsApp - send to fixed number from properties
        Message.creator(
                new com.twilio.type.PhoneNumber(toNumber),
                new com.twilio.type.PhoneNumber(fromNumber),
                messageBody
        ).create();

        System.out.println("✅ Registration notification sent to fixed email and WhatsApp.");
    }

    // Send WhatsApp alert to fixed number
    public void sendWhatsAppAlert(Alert alert) {
        String messageBody = "⚠️ *NeuroGuard Alert!*\n\n"
                + alert.getMessage() + "\n\n"
                + "🕒 Time: " + alert.getTimestamp();

        System.out.println("Sending WhatsApp alert to fixed number: " + toNumber);
        Message.creator(
                new com.twilio.type.PhoneNumber(toNumber),
                new com.twilio.type.PhoneNumber(fromNumber),
                messageBody
        ).create();
    }

    // Send alert (email + WhatsApp) with patient details and current vitals
    public void sendAlert(Patient patient, Caregiver caregiver, Alert alert) {

        String formattedMessage = String.format(
            "NeuroGuard Alert!\n\n" +
            "Patient Name: %s\n" +
            "Patient ID: %s\n" +
            "Alert Type: %s\n\n" +
            "Current Vitals:\n" +
            "• Heart Rate: %.1f bpm\n" +
            "• SpO₂: %.1f%%\n" +
            "• Temperature: %.1f°C\n" +
            "Location: https://maps.google.com/?q=%.6f,%.6f\n" +
            "🕒 Time: %s",
            patient.getName(),
            patient.getId(),
            alert.getType().toUpperCase(),
            alert.getHeartRate(),
            alert.getSpo2(),
            alert.getTemperature(),
            alert.getLatitude(),
            alert.getLongitude(),
            alert.getTimestamp()
        );

        System.out.println("Formatted alert message:\n" + formattedMessage);

        // WhatsApp - send to fixed number
        Message.creator(
            new com.twilio.type.PhoneNumber(toNumber),
            new com.twilio.type.PhoneNumber(fromNumber),
            formattedMessage
        ).create();

        // Email - send to caregiver
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(caregiver.getEmail());
        email.setSubject("⚠️ NeuroGuard Alert: " + alert.getType());
        email.setText(formattedMessage);
        mailSender.send(email);

        System.out.println("✅ Alert sent via email and WhatsApp.");
    }
}
