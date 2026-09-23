package io.github.gautamKodes.saas_usage_guardian;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulateCallController {

    private final EventProducer eventProducer;
    private final PricingConfig pricingConfig;

    public SimulateCallController(EventProducer eventProducer, PricingConfig pricingConfig) {
        this.eventProducer = eventProducer;
        this.pricingConfig = pricingConfig;
    }

    @PostMapping("/simulate-call")
    public ResponseEntity<Void> simulateCall(@RequestBody SimulateApiCall request) {
        double cost = pricingConfig.getCost(request.getVendor());

        ApiCallEvent event = ApiCallEvent.builder()
                .vendor(request.getVendor())
                .callerId(request.getCallerId())
                .cost(cost)
                .timestamp(System.currentTimeMillis())
                .build();

        eventProducer.publish(event);
        return ResponseEntity.ok().build();
    }
}
