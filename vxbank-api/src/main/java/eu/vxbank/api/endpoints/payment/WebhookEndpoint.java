package eu.vxbank.api.endpoints.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys vxStripeKeys;

    @PostMapping("/stripeWebhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String stripeSignature) throws
            SignatureVerificationException {
        // Verify the Stripe webhook signature
        Event event = Webhook.constructEvent(payload, stripeSignature, vxStripeKeys.webhookSigningSecret);


        // Process the Stripe event based on the payload
        // Update your system based on the event

        return ResponseEntity.ok("Webhook received and processed.");
    }


}
