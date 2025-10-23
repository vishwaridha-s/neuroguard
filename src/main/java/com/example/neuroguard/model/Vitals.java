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
@Document(collection = "vitals")
public class Vitals {
    @Id private String id;
    private String patientId;
    private LocalDateTime timestamp;
    private double heartRate;
    private double spo2;
    private double accelX;
    private double accelY;
    private double accelZ;
    private double gyroX;
    private double gyroY;
    private double gyroZ;
    private double temperature;
    private double latitude;
    private double longitude;
    private String mlPrediction;
    private boolean alerted;
}

