package com.example.neuroguard.services;

import com.example.neuroguard.model.Vitals;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MLService {

    @Value("${neuroguard.ml.url}")
    private String mlUrl;

    private final RestTemplate restTemplate;

    public MLService() {
        this.restTemplate = createJsonOnlyRestTemplate();
    }

    private RestTemplate createJsonOnlyRestTemplate() {
        RestTemplate template = new RestTemplate();
        // Remove all existing message converters.
        template.getMessageConverters().clear();
        // Add JSON converter only.
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        template.getMessageConverters().add(converter);
        return template;
    }

    public String predict(Vitals vitals) {
        Map<String, Object> payload = new LinkedHashMap<>();
        // Keep keys as expected by the ML model JSON payload
        payload.put("heart_rate", vitals.getHeartRate());
        payload.put("spo2", vitals.getSpo2());
        payload.put("accel_x", vitals.getAccelX());
        payload.put("accel_y", vitals.getAccelY());
        payload.put("accel_z", vitals.getAccelZ());
        payload.put("gyro_x", vitals.getGyroX());
        payload.put("gyro_y", vitals.getGyroY());
        payload.put("gyro_z", vitals.getGyroZ());
        payload.put("temperature", vitals.getTemperature());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Tell server we send JSON
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // Tell server we want JSON back

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    mlUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getBody() != null && response.getBody().containsKey("prediction")) {
                return response.getBody().get("prediction").toString();
            } else {
                return "normal";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "normal";
        }
    }
}
