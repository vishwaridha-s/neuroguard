package com.example.neuroguard.model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "caregivers")
public class Caregiver {
    @Id private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private List<String> patientIds;
}

