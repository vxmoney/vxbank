package eu.vxbank.api.controlers;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentsEndpoint {

    @PostMapping("/payments/create-checkout-session")
    public ResponseEntity<String> createCheckoutSession() throws StripeException {
        String YOUR_DOMAIN = "http://localhost:4242";
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(YOUR_DOMAIN + "/success.html")
                        .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
                        .setAutomaticTax(
                                SessionCreateParams.AutomaticTax.builder()
                                        .setEnabled(true)
                                        .build())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        // Provide the exact Price ID (for example, pr_1234) of the product you want to sell
                                        .setPrice("{{PRICE_ID}}")
                                        .build())
                        .build();
        Session session = Session.create(params);
        String response = session.getUrl();
        response = "Hello checkout response: " + response;
        return ResponseEntity.ok(response);
    }
}
