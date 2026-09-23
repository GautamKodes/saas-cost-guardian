package com.saasguardian.decision;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SlackNotifier {

    private final RestClient restClient;

    public SlackNotifier() {
        this.restClient = RestClient.create();
    }

    public void sendAlert(int calls, double baseline, double score) {

        String message = String.format(
                "🚨 SaaS Cost Guardian Alert! Calls: %d vs Baseline: %.0f | Anomaly Score: %.2f",
                calls,
                baseline,
                score
        );

        System.out.println("SLACK ALERT: " + message);

        // Slack webhook will be connected here later.
        // Do NOT put the webhook URL directly in the source code.
    }
}