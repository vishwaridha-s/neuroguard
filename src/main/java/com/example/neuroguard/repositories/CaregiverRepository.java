package com.example.neuroguard.repositories;
import com.example.neuroguard.model.Caregiver;
import org.springframework.data.mongodb.repository.MongoRepository; 
import java.util.Optional;
public interface CaregiverRepository extends MongoRepository<Caregiver, String> {
    Optional<Caregiver> findByEmail(String email);
}

