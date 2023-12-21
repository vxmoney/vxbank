package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateParams;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class EventResultHelper {
    public static EventResultCreateResponse create(TestRestTemplate restTemplate,
                                                   int port,
                                                   String vxToken,
                                                   EventResultCreateParams params,
                                                   int expectedStatusCode) {


        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<EventResultCreateParams> requestEntity = new HttpEntity<>(params, headers);

        // Build the uri
        // Build the URL with query parameters
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/eventresult");


        // Make the POST request
        ResponseEntity<EventResultCreateResponse> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                EventResultCreateResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        EventResultCreateResponse response = responseEntity.getBody();
        return response;
    }
}
