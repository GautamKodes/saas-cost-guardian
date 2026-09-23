
package io.github.gautamKodes.saas_usage_guardian;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class RedisAggregator {

    private final StringRedisTemplate redisTemplate;
    private static final DateTimeFormatter HOUR_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHH").withZone(ZoneId.of("UTC"));

    public RedisAggregator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void update(ApiCallEvent event) {
        String currentHour = HOUR_FORMATTER.format(Instant.ofEpochMilli(event.getTimestamp()));
        String vendor = event.getVendor().toLowerCase();

        String callsKey = "calls:" + vendor + ":" + currentHour;
        String costKey = "cost:" + vendor + ":" + currentHour;
        String callersKey = "callers:" + vendor + ":" + currentHour;

        redisTemplate.opsForValue().increment(callsKey, 1);


        redisTemplate.opsForValue().increment(costKey, event.getCost());

        redisTemplate.opsForSet().add(callersKey, event.getCallerId());
    }
}