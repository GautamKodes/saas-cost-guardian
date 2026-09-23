package io.github.gautamKodes.saas_usage_guardian;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventConsumer {
    private final RedisAggregator redisAggregator;

    public EventConsumer(RedisAggregator redisAggregator) {
        this.redisAggregator = redisAggregator;
    }

    @KafkaListener(topics = "api-events", groupId = "guardian-group")
    public void consume(ApiCallEvent event) {
        log.info("Received event from Kafka: {}", event);

        redisAggregator.update(event);
    }
}
