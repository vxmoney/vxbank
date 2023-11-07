package eu.vxbank.api.controlers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentEndpoints {

    @PostMapping("/payments/create-checkout-session")
    public String createCheckoutSession(){
        return "Hello checkout session";
    }
}
