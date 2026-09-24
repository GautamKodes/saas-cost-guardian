package com.saasguardian.decision;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class StatsClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String STATS_URL = "http://localhost:8081/stats";

    public StatsData getStats() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> response = restTemplate.getForObject(STATS_URL, Map.class);
            if (response != null) {
                int totalCalls = 0;
                int uniqueCallers = 0;
                double totalCost = 0.0;

                for (Map<String, Object> vendorData : response.values()) {
                    if (vendorData.containsKey("calls")) {
                        totalCalls += ((Number) vendorData.get("calls")).intValue();
                    }
                    if (vendorData.containsKey("uniqueCallers")) {
                        uniqueCallers += ((Number) vendorData.get("uniqueCallers")).intValue();
                    }
                    if (vendorData.containsKey("cost")) {
                        totalCost += ((Number) vendorData.get("cost")).doubleValue();
                    }
                }

                return new StatsData(totalCalls, 20.0, uniqueCallers, totalCost);
            }
        } catch (Exception e) {
            // Fallback if capture service is offline
        }
        return new StatsData(0, 20.0, 0, 0.0);
    }

    public record StatsData(
            int calls,
            double baselineCalls,
            int uniqueCallers,
            double cost
    ) {
    }
}