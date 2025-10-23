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
@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    private String name;
    private int age;
    private String sex;
    private String email;
    private String phoneNumber;
    private String homeAddress;

    private String caregiverEmail;
    private String caregiverPhone;
    private String caregiverId;

    // Unique code for linking with caregiver or verification
    private String code;

    private LocalDateTime createdAt;
}
