package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PublicEventHelper {

    public static PublicEventCreateResponse create(TestRestTemplate restTemplate,
                                                   int port,
                                                   String vxToken,
                                                   PublicEventCreateParams params,
                                                   int expectedStatusCode) {


        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<PublicEventCreateParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<PublicEventCreateResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEvent", HttpMethod.POST, requestEntity, PublicEventCreateResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        PublicEventCreateResponse response = responseEntity.getBody();
        return response;
    }
}
