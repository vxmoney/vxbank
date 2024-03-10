package eu.vxbank.api.endpoints.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDepositFiatParams {
    public Long userId;
    public Long amount;
    public String currency;
}
