package com.example.neuroguard.repositories;
import com.example.neuroguard.model.Alert;
import org.springframework.data.mongodb.repository.MongoRepository; 
import java.util.List;
public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findByPatientIdOrderByTimestampDesc(String patientId);
}

