package com.saasguardian.decision;

import org.springframework.stereotype.Component;

@Component
public class ScoreClient {

    public double getScore() {
        // Temporary mock data.
        // This will later call Person B's /score endpoint.
        return 0.82;
    }
}