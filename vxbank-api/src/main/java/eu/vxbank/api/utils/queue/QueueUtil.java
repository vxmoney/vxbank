package eu.vxbank.api.utils.queue;

import com.google.cloud.tasks.v2.*;
import com.google.protobuf.ByteString;
import eu.vxbank.api.endpoints.payment.WebhookEndpoint;
import eu.vxbank.api.utils.ApiConstants;
import eu.vxbank.api.utils.components.SystemService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class QueueUtil {

    private static final Logger logger = Logger.getLogger(QueueUtil.class.getName());

    public static void pushToHandleCheckoutSessionCompleted(SystemService systemService, String dataPayload) {


        // Retrieve project ID
        String projectId = systemService.getProjectId();
        if (projectId.equals(ApiConstants.APPLICATION_ID_LOCALHOST)){
            // do nothing just return
            return;
        }
        // location id: gcloud app describe --format="value(locationId)"
        String locationId = "europe-west1";
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
}
