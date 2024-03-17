package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.payment.dto.HandleCheckoutSessionCompletedDto;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class WebhookHelper {
    public static String handleStripeWebhook(TestRestTemplate restTemplate,
                                             int port,
                                             String stripeSignature,
                                             String body,
                                             int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Stripe-Signature", stripeSignature); // Add your Stripe signature here

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/stripeWebhook", request, String.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        String response = responseEntity.getBody();
        return response;

    }

    public static void handleCheckoutSessionCompleted(TestRestTemplate restTemplate,
                                                      int port,
                                                      HandleCheckoutSessionCompletedDto params,
                                                      int expectedStatusCode) {
        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        // headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<HandleCheckoutSessionCompletedDto> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:" + port + "/handleCheckoutSessionCompleted",
                HttpMethod.POST,
                requestEntity,
                String.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
    }
}
