package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class StripeConfigHelper {
    public static StripeConfigGetByUserIdResponse getByUserId(Long userId,
                                                              String vxToken,
                                                              TestRestTemplate restTemplate,
                                                              int port,
                                                              int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<StripeConfigGetByUserIdResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/stripeConfig/getByUserId/" + userId,
                HttpMethod.GET,
                requestEntity,
                StripeConfigGetByUserIdResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        StripeConfigGetByUserIdResponse responseBody = responseEntity.getBody();
        return responseBody;

    }


    public static StripeConfigInitiateConfigParams initiateConfig(String vxToken,
                                                                  StripeConfigInitiateConfigParams initiateConfigParams,
                                                                  TestRestTemplate restTemplate,
                                                                  int port,
                                                                  int expectedStatusCode) {

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the request body and headers
        HttpEntity<StripeConfigInitiateConfigParams> requestEntity = new HttpEntity<>(initiateConfigParams, headers);

        ResponseEntity<StripeConfigInitiateConfigParams> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/stripeConfig/initiateConfig",
                HttpMethod.POST,
                requestEntity,
                StripeConfigInitiateConfigParams.class
        );

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        StripeConfigInitiateConfigParams responseBody = responseEntity.getBody();
        return responseBody;


    }
}
