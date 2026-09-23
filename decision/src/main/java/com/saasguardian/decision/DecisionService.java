package com.saasguardian.decision;

import org.springframework.stereotype.Service;

@Service
public class DecisionService {

    private static final double ALERT_THRESHOLD = 0.7;

    public void evaluate(double score, int calls, double baseline) {

        System.out.println("----- Decision Check -----");
        System.out.println("Current calls: " + calls);
        System.out.println("Baseline calls: " + baseline);
        System.out.println("Anomaly score: " + score);

        if (score > ALERT_THRESHOLD) {
            System.out.println("🚨 ALERT: Anomaly detected!");
        } else {
            System.out.println("Traffic is normal.");
        }
    }
}