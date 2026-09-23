package io.github.gautamKodes.saas_usage_guardian;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StatsController {

    private final StringRedisTemplate redisTemplate;

    private static final DateTimeFormatter HOUR_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHH").withZone(ZoneId.of("UTC"));

    public StatsController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/stats")
    public Map<String, Map<String, Object>> getStats() {
        String currentHour = HOUR_FORMATTER.format(Instant.now());
        List<String> vendors = List.of("openai", "twilio");

        Map<String, Map<String, Object>> result = new HashMap<>();

        for (String vendor : vendors) {
            String callsKey = "calls:" + vendor + ":" + currentHour;
            String costKey = "cost:" + vendor + ":" + currentHour;
            String callersKey = "callers:" + vendor + ":" + currentHour;

            String callsVal = redisTemplate.opsForValue().get(callsKey);
            String costVal = redisTemplate.opsForValue().get(costKey);
            Long uniqueCallersVal = redisTemplate.opsForSet().size(callersKey);
            long calls = (callsVal != null) ? Long.parseLong(callsVal) : 0;
            double cost = (costVal != null) ? Double.parseDouble(costVal) : 0.0;
            long uniqueCallers = (uniqueCallersVal != null) ? uniqueCallersVal : 0;

            Map<String, Object> vendorStats = new HashMap<>();
            vendorStats.put("calls", calls);
            vendorStats.put("cost", cost);
            vendorStats.put("uniqueCallers", uniqueCallers);

            result.put(vendor, vendorStats);
        }

        return result;
    }
}