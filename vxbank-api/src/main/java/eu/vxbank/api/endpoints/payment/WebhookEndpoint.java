package eu.vxbank.api.endpoints.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
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
        Event event = Webhook.constructEvent(payload,
                stripeSignature,
                vxStripeKeys.webhookSigningSecret,
                vxStripeKeys.tolerance);


        // Process the Stripe event based on the payload
        // Update your system based on the event
        String id = event.getId();
        String type = event.getType();
        System.out.println("Signature validated: id=" + id + ", type=" + type);

        if ("checkout.session.completed".equals(event.getType())) {
            // Access the session ID from the event data
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .get();
            String sessionId = session.getId();

            // Now you have the session ID, and you can use it as needed
            System.out.println("Session ID: " + sessionId);
        }

        return ResponseEntity.ok("Webhook received and processed.");
    }


}
