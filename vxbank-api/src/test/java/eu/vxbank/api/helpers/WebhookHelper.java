package eu.vxbank.api.helpers;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/stripeWebhook", request, String.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        String response = responseEntity.getBody();
        return response;

    }
}
