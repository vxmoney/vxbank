package eu.vxbank.api.endpoints.payment;

import com.google.cloud.ServiceOptions;
import com.google.cloud.tasks.v2.*;
import com.google.protobuf.ByteString;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import eu.vxbank.api.endpoints.payment.dto.HandleCheckoutSessionCompletedDto;
import eu.vxbank.api.utils.ApiConstants;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.queue.QueueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;


@RestController
public class WebhookEndpoint {
    private static final Logger logger = Logger.getLogger(WebhookEndpoint.class.getName());


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

            //try(CloudTaskClient)
            HandleCheckoutSessionCompletedDto dto = new HandleCheckoutSessionCompletedDto();
            dto.payload = payload;
            dto.stripeSignature = stripeSignature;
            QueueUtil.pushToHandleCheckoutSessionCompleted(systemService, dto);
        }

        return ResponseEntity.ok("Webhook received and processed.");
    }


    @PostMapping("/handleCheckoutSessionCompleted")
    public void handleCheckoutSessionCompleted(@RequestBody HandleCheckoutSessionCompletedDto dto) throws
            SignatureVerificationException {
        // Process the task (e.g., perform some computation, update the database, etc.)
        Event event = Webhook.constructEvent(dto.payload,
                dto.stripeSignature,
                vxStripeKeys.webhookSigningSecret,
                vxStripeKeys.tolerance);

        if (!"checkout.session.completed".equals(event.getType())) {
            throw new IllegalStateException("Not checkout.session.completed event");
        }


        // Access the session ID from the event data
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .get();
        String sessionId = session.getId();

        // Now you have the session ID, and you can use it as needed

        logger.info("Time to process sessionId: " + sessionId);

    }


}
