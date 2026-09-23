package io.github.gautamKodes.saas_usage_guardian;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulateApiCall {
    private String vendor;
    private String callerId;
}
