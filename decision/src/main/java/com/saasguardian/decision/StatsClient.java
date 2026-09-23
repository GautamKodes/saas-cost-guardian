package com.saasguardian.decision;

import org.springframework.stereotype.Component;

@Component
public class StatsClient {

    public StatsData getStats() {
        // Temporary mock data.
        // This will later call Person A's GET /stats endpoint.
        return new StatsData(
                150,
                100,
                12,
                0.45
        );
    }

    public record StatsData(
            int calls,
            double baselineCalls,
            int uniqueCallers,
            double cost
    ) {
    }
}