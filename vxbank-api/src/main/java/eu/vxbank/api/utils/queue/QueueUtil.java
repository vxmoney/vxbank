package eu.vxbank.api.utils.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.tasks.v2.*;
import com.google.protobuf.ByteString;
import eu.vxbank.api.endpoints.payment.WebhookEndpoint;
import eu.vxbank.api.endpoints.payment.dto.HandleCheckoutSessionCompletedDto;
import eu.vxbank.api.utils.ApiConstants;
import eu.vxbank.api.utils.components.SystemService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class QueueUtil {

    private static final Logger logger = Logger.getLogger(QueueUtil.class.getName());

    public static void pushToHandleCheckoutSessionCompleted(SystemService systemService,
                                                            HandleCheckoutSessionCompletedDto handleCheckoutSessionCompletedDto) {


        // Retrieve project ID
        String projectId = systemService.getProjectId();
        if (projectId.equals(ApiConstants.APPLICATION_ID_LOCALHOST)) {
            // do nothing just return
            return;
        }
        // location id: gcloud app describe --format="value(locationId)"
        String locationId = "europe-west1";
        String queueId = "handle-checkout-session";

        try (CloudTasksClient client = CloudTasksClient.create()) {

            String url = systemService.getHandleCheckoutSessionCompletedQueueUrl();

            // Construct the fully qualified queue name.
            String queuePath = QueueName.of(projectId, locationId, queueId)
                    .toString();

            ObjectMapper objectMapper = new ObjectMapper();
            String dataPayload = objectMapper.writeValueAsString(handleCheckoutSessionCompletedDto);
            byte[] payloadBytes = dataPayload.getBytes(StandardCharsets.UTF_8);

            // Construct the task body.
            Task.Builder taskBuilder = Task.newBuilder()
                    .setHttpRequest(HttpRequest.newBuilder()
                            //.setBody(ByteString.copyFrom(dataPayload, Charset.defaultCharset()))
                            .setBody(ByteString.copyFrom(payloadBytes))
                            .setUrl(url)
                            .setHttpMethod(HttpMethod.POST)
                            .putHeaders("Content-Type", "application/json")
                            .build());

            // Send create task request.
            Task task = client.createTask(queuePath, taskBuilder.build());
            System.out.println("Task created: " + task.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
