package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatResponse;
import eu.vxbank.api.endpoints.ping.dto.PingInitiateVxGamingResponse;
import eu.vxbank.api.endpoints.ping.dto.PingRequestFundsParams;
import eu.vxbank.api.endpoints.ping.dto.PingRequestFundsResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PaymentHelper {
    public static PaymentDepositFiatResponse depositFiat(TestRestTemplate restTemplate,
                                                         int port,
                                                         String vxToken,
                                                         PaymentDepositFiatParams params,
                                                         int expectedStatusCode) {

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<PaymentDepositFiatParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<PaymentDepositFiatResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/payment/depositFiat",
                HttpMethod.POST,
                requestEntity,
                PaymentDepositFiatResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        PaymentDepositFiatResponse response = responseEntity.getBody();
        return response;

    }
}
