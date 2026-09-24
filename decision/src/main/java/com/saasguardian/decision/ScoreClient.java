package com.saasguardian.decision;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Component
public class ScoreClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String SCORE_URL = "http://localhost:8000/score";

    public double getScore() {
        return getScore(0, 0);
    }

    public double getScore(int currentCalls, int uniqueCallers) {
        if (currentCalls == 0) {
            return 0.0;
        }
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("calls_last_window", (double) currentCalls);
            req.put("prev_window_avg", 20.0);
            req.put("unique_callers", (double) uniqueCallers);

            double concentration = (currentCalls > 10 && uniqueCallers <= 2) ? 0.85 : 0.15;
            req.put("top_caller_concentration", concentration);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(SCORE_URL, req, Map.class);
            if (response != null && response.containsKey("score")) {
                return ((Number) response.get("score")).doubleValue();
            }
        } catch (Exception e) {
            // Fallback if ML service is offline
        }
        return 0.10;
    }
}