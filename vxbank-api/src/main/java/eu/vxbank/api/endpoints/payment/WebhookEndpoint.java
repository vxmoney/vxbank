package eu.vxbank.api.endpoints.payment;

import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.LocationName;
import com.google.cloud.tasks.v2.Queue;
import com.google.cloud.tasks.v2.QueueName;
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

import java.io.IOException;


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

            //try(CloudTaskClient)
            pushToHandleCheckoutSessionCompleted(sessionId);
        }

        return ResponseEntity.ok("Webhook received and processed.");
    }

    private void pushToHandleCheckoutSessionCompleted(String sessionId) {
        try (CloudTasksClient client = CloudTasksClient.create()) {

            String projectId = "projectId";
            String locationId = "locationId";
            String queueId = "handle-checkout-session";

            // Construct the fully qualified location.
            String parent = LocationName.of(projectId, locationId)
                    .toString();

            // Construct the fully qualified queue path.
            String queuePath = QueueName.of(projectId, locationId, queueId)
                    .toString();

            // Send create queue request.
            Queue queue = client.createQueue(parent,
                    Queue.newBuilder()
                            .setName(queuePath)
                            .build());

            System.out.println("Queue created: " + queue.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/handleCheckoutSessionCompleted")
    public String handleCheckoutSessionCompleted(@RequestBody String taskData) {
        // Process the task (e.g., perform some computation, update the database, etc.)
        System.out.println("Log handleCheckoutSessionCompleted");
        return "Task processed successfully.";
    }


}
