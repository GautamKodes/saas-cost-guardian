package com.saasguardian.decision;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
public class DecisionController {

    private final StatsClient statsClient;
    private final ScoreClient scoreClient;

    public DecisionController(
            StatsClient statsClient,
            ScoreClient scoreClient) {

        this.statsClient = statsClient;
        this.scoreClient = scoreClient;
    }

    @GetMapping("/decision")
    public DecisionResponse getDecision() {

        StatsClient.StatsData stats = statsClient.getStats();
        double score = scoreClient.getScore();

        boolean anomaly = score > 0.7;

        return new DecisionResponse(
                score,
                stats.calls(),
                stats.baselineCalls(),
                anomaly
        );
    }

    public record DecisionResponse(
            double score,
            int calls,
            double baseline,
            boolean anomaly
    ) {
    }
}