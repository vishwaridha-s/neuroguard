package com.example.neuroguard.repositories;
import com.example.neuroguard.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByCode(String code);
}

