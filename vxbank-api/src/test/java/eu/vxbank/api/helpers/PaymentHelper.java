package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class PaymentHelper {
    public static PaymentDepositFiatResponse depositFiat(TestRestTemplate restTemplate, int port, String vxToken, PaymentDepositFiatParams params, int i) {
        throw new IllegalStateException("Please implement this");
    }
}
