package com.saasguardian.decision;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DecisionScheduler {

    private final DecisionService decisionService;
    private final StatsClient statsClient;
    private final ScoreClient scoreClient;
    private final SlackNotifier slackNotifier;

    private boolean alertSent = false;

    public DecisionScheduler(
            DecisionService decisionService,
            StatsClient statsClient,
            ScoreClient scoreClient,
            SlackNotifier slackNotifier) {

        this.decisionService = decisionService;
        this.statsClient = statsClient;
        this.scoreClient = scoreClient;
        this.slackNotifier = slackNotifier;
    }

    @Scheduled(fixedRate = 5000)
    public void checkForAnomaly() {

        StatsClient.StatsData stats = statsClient.getStats();

        double score = scoreClient.getScore();

        decisionService.evaluate(
                score,
                stats.calls(),
                stats.baselineCalls()
        );

        // Trigger alert only once when anomaly is first detected
        if (score > 0.7 && !alertSent) {

            slackNotifier.sendAlert(
                    stats.calls(),
                    stats.baselineCalls(),
                    score
            );

            alertSent = true;

        } else if (score <= 0.7) {

            // Reset alert state when system returns to normal
            alertSent = false;
        }
    }
}