package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.eventparticipant.dto.EventParticipantGetByEventIdResponse;
import eu.vxbank.api.endpoints.ping.dto.PingRequestFundsParams;
import eu.vxbank.api.endpoints.ping.dto.PingRequestFundsResponse;
import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PingHelper {

    public static LoginResponse whoAmI(String vxToken,
                                       TestRestTemplate restTemplate,
                                       int port,
                                       int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/ping/whoAmI", HttpMethod.GET, requestEntity, LoginResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        LoginResponse responseBody = responseEntity.getBody();
        return responseBody;

    }

    public static PingResponse getEnvironment(TestRestTemplate restTemplate, int port, int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<PingResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/ping/getEnvironment", HttpMethod.GET, requestEntity, PingResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        PingResponse responseBody = responseEntity.getBody();
        return responseBody;

    }


    public static PingRequestFundsResponse requestFunds(TestRestTemplate restTemplate,
                                                        int port,
                                                        String vxToken,
                                                        PingRequestFundsParams params,
                                                        int expectedStatusCode) {

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<PingRequestFundsParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<PingRequestFundsResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/ping/requestFunds",
                HttpMethod.POST,
                requestEntity,
                PingRequestFundsResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        PingRequestFundsResponse response = responseEntity.getBody();
        return response;

    }
}
