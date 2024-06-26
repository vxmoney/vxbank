package eu.vxbank.api.endpoints.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateParams {
    public Long vxUserId;
    public Long vxStripeConfigId;
    public Long vxPaymentId;
}
