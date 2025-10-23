package com.example.neuroguard.model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "alerts")
public class Alert {
    @Id private String id;
    private String patientId;
    private String caregiverId;
    private LocalDateTime timestamp;
    private String message;
    private double heartRate;
    private double spo2;
    private double temperature;
    private double latitude;
    private double longitude;
    private String type;
}

