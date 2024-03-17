package eu.vxbank.api.endpoints.payment;

import com.google.cloud.ServiceOptions;
import com.google.cloud.tasks.v2.*;
import com.google.protobuf.ByteString;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import eu.vxbank.api.endpoints.payment.dto.HandleCheckoutSessionCompletedDto;
import eu.vxbank.api.utils.ApiConstants;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.queue.QueueUtil;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.data.models.VxEventPayment;
import vxbank.datastore.data.service.VxDsService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
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
            StripeException {
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

            String paymentIntentId = session.getPaymentIntent();
            PaymentIntent paymentIntent = VxStripeUtil.getPaymentIntentByPaymentId(vxStripeKeys.stripeSecretKey,
                    paymentIntentId);
            //Charge  paymentIntent.getLatestCharge();

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
            StripeException {

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
        String stripeSessionId = session.getId();
        List<VxEventPayment> eventPayments = VxDsService.getByStripeSessionId(VxEventPayment.class,
                systemService.getVxBankDatastore(),
                stripeSessionId);
        if (eventPayments.size() != 1) {
            throw new IllegalStateException("Better call Bogdan. This should never be the case");
        }

        // Now you have the session ID, and you can use it as needed
        String paymentIntentId = session.getPaymentIntent();
        PaymentIntent paymentIntent = VxStripeUtil.getPaymentIntentByPaymentId(vxStripeKeys.stripeSecretKey,
                paymentIntentId);

        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new IllegalStateException("Payment not yet completed");
        }
        // Payment is successful, you can now access the fees deducted by Stripe
        Long stripeFee = paymentIntent.getApplicationFeeAmount(); // This returns the amount in cents
        // Convert to a readable amount if needed
        //double feeInDollars = stripeFee / 100.0;

        // Now 'feeInDollars' contains the deducted Stripe fees
        logger.info("stripeSessionId: "+ stripeSessionId);
        logger.info("paymentIntentId: "+ paymentIntentId);
        logger.info("stripeFee: "+ stripeFee);

        logger.info("Time to finish implementing this DEBUG");

    }


}
