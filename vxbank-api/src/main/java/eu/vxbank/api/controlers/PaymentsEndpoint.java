package eu.vxbank.api.controlers;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


import eu.vxbank.api.controlers.models.createpaymentintent.CreatePaymentIntentParams;
import eu.vxbank.api.controlers.models.createpaymentintent.StripeSessionResponse;
import eu.vxbank.api.utils.components.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PaymentsEndpoint {

    @Autowired
    SystemService systemService;

    @PostMapping("/payments/create-checkout-session")
    public ResponseEntity<Void> createCheckoutSession() throws StripeException {

        Stripe.apiKey =
                "sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b";

        String YOUR_DOMAIN = "http://localhost:8080";
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(YOUR_DOMAIN + "/success.html")
                .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
                .setAutomaticTax(SessionCreateParams.AutomaticTax.builder()
                        .setEnabled(true)
                        .build())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        // Provide the exact Price ID (for example, pr_1234) of the product you want to sell
                        .setPrice("price_1O9xgQB6aHGAQTGCliE3NECU")
                        .build())
                .build();
        Session session = Session.create(params);


        String redirectUrl = session.getUrl();
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", redirectUrl)
                .build();
    }

    @PostMapping("/payments/create-payment-intent")
    @ResponseBody
    public StripeSessionResponse createPaymentIntentIntent(
            @RequestBody CreatePaymentIntentParams createParams
    ) throws StripeException {

        Stripe.apiKey =
                "sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b";



        // Line item details
        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", "eur");
        priceData.put("product_data", Map.of("name", "Test Product 2 Name"));
        priceData.put("unit_amount", 3000);

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price_data", priceData);
        lineItem.put("quantity", 1);

        // Line items list
        List<Object> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        // Session parameters
        Map<String, Object> params = new HashMap<>();
        params.put("line_items", lineItems);
        params.put("success_url", "https://example.com/success");
        params.put("mode", "payment");

        Session session = Session.create(params);
        System.out.println("Checkout Session URL: " + session.getUrl());
        System.out.println("StripeSessionId = " + session.getId());

        StripeSessionResponse stripeSessionResponse = new StripeSessionResponse();
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();

        return stripeSessionResponse;
    }

}
