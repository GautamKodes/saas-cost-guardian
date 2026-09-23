package io.github.gautamKodes.saas_usage_guardian;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallEvent {
    private String vendor;
    private String callerId;
    private double cost;
    private long timestamp;
}
