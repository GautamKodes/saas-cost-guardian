package com.saasguardian.decision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecisionService {

    private static final double ALERT_THRESHOLD = 0.45;

    @Autowired
    private EmailSendService emailSendService;

    public void evaluate(double score, int calls, double baseline) {

        System.out.println("----- Decision Check -----");
        System.out.println("Current calls: " + calls);
        System.out.println("Baseline calls: " + baseline);
        System.out.println("Anomaly score: " + score);

        if (score > ALERT_THRESHOLD) {
            System.out.println("🚨 ALERT: Anomaly detected!");
//            emailSendService.sendEmail("gk3902956@gmail.com", "Test", "Test successful!");
        } else {
            System.out.println("Traffic is normal.");
        }
    }
}