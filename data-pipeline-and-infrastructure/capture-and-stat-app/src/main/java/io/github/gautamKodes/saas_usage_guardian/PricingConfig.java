package io.github.gautamKodes.saas_usage_guardian;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class PricingConfig {

    private final Map<String, Double> pricingTable = Map.of(
            "openai", 0.002,
            "twilio", 0.0075
    );

    public double getCost(String vendor){
        if (vendor == null) return 0.001;
        return pricingTable.getOrDefault(vendor.toLowerCase(), 0.0001);
    }
}
