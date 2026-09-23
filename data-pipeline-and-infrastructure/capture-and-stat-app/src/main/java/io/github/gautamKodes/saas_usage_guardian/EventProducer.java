package io.github.gautamKodes.saas_usage_guardian;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@Slf4j
public class EventProducer {
//    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "api-events";

    public EventProducer(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ApiCallEvent event){
        try {
            kafkaTemplate.send(TOPIC, event.getVendor(), event);
            log.info("Published event to kafka: {}", event);
        } catch (Exception e){
            log.error("Failed to send event to kafka: {}", e.getMessage(), e);
        }
    }
}
