package com.saasguardian.decision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DecisionScheduler {

    private final DecisionService decisionService;
    private final StatsClient statsClient;
    private final ScoreClient scoreClient;
    private final SlackNotifier slackNotifier;
//    @Autowired
    private final EmailSendService emailSendService;

    private boolean alertSent = false;

    public DecisionScheduler(
            DecisionService decisionService,
            StatsClient statsClient,
            ScoreClient scoreClient,
            SlackNotifier slackNotifier,
            EmailSendService emailSendService) {

        this.decisionService = decisionService;
        this.statsClient = statsClient;
        this.scoreClient = scoreClient;
        this.slackNotifier = slackNotifier;
        this.emailSendService = emailSendService;
    }

    @Scheduled(fixedRate = 5000)
    public void checkForAnomaly() {

        StatsClient.StatsData stats = statsClient.getStats();

        double score = scoreClient.getScore(stats.calls(), stats.uniqueCallers());

        decisionService.evaluate(
                score,
                stats.calls(),
                stats.baselineCalls()
        );

        // Trigger alert only once when anomaly is first detected
        if (score > 0.45 && !alertSent) {

            slackNotifier.sendAlert(
                    stats.calls(),
                    stats.baselineCalls(),
                    score
            );

            emailSendService.sendEmail("gk3902956@gmail.com", "Anomaly alert", "Anomaly detected");

            alertSent = true;

        } else if (score <= 0.45) {

            // Reset alert state when system returns to normal
            alertSent = false;
        }
    }
}