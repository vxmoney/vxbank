package eu.vxbank.api.endpoints.ping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PingRequestFundsParams {
    Long userId;
    Long amount;
    String currency;

}
