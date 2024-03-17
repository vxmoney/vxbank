package eu.vxbank.api.endpoints.payment;

import com.google.cloud.ServiceOptions;
import com.google.cloud.tasks.v2.*;
import com.google.protobuf.ByteString;
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
            pushToHandleCheckoutSessionCompleted(sessionId);
        }

        return ResponseEntity.ok("Webhook received and processed.");
    }

    private void pushToHandleCheckoutSessionCompleted(String dataPayload) {

        // Retrieve project ID
        String projectId = systemService.getProjectId();
        // location id: gcloud app describe --format="value(locationId)"
        String locationId = "europe-west";
        String queueId = "handle-checkout-session";

        try (CloudTasksClient client = CloudTasksClient.create()) {
            logger.info("pushToHandleCheckoutSessionCompleted");

            String url = systemService.getHandleCheckoutSessionCompletedQueueUrl();
            String payload = "Hello, World!";

            // Construct the fully qualified queue name.
            String queuePath = QueueName.of(projectId, locationId, queueId)
                    .toString();

            // Construct the task body.
            Task.Builder taskBuilder = Task.newBuilder()
                    .setHttpRequest(HttpRequest.newBuilder()
                            .setBody(ByteString.copyFrom(payload, Charset.defaultCharset()))
                            .setUrl(url)
                            .setHttpMethod(HttpMethod.POST)
                            .build());

            // Send create task request.
            Task task = client.createTask(queuePath, taskBuilder.build());
            System.out.println("Task created: " + task.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/handleCheckoutSessionCompleted")
    public String handleCheckoutSessionCompleted(@RequestBody String taskData) {
        // Process the task (e.g., perform some computation, update the database, etc.)
        System.out.println("Log handleCheckoutSessionCompleted");
        logger.info("handleCheckoutSessionCompleted");
        return "Task processed successfully.";
    }


}
