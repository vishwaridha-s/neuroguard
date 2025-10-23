package com.example.neuroguard.repositories;
import com.example.neuroguard.model.Vitals;
import org.springframework.data.mongodb.repository.MongoRepository; 
import java.util.List;
public interface VitalsRepository extends MongoRepository<Vitals, String> {
    List<Vitals> findByPatientIdOrderByTimestampDesc(String patientId);
    List<Vitals> findByMlPredictionIsNull();
}

