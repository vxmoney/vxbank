package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.event.dto.EventGetResponse;
import eu.vxbank.api.endpoints.event.dto.EventSearchResponse;
import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import vxbank.datastore.data.models.VxEvent;

import java.util.List;

public class EventHelper {
    public static EventCreateResponse create(TestRestTemplate restTemplate,
                                             int port,
                                             String vxToken,
                                             EventCreateParams params,
                                             int expectedStatusCode) {


        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<EventCreateParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<EventCreateResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/event", HttpMethod.POST, requestEntity, EventCreateResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        EventCreateResponse response = responseEntity.getBody();
        return response;
    }

    public static EventGetResponse get(TestRestTemplate restTemplate,
                                       int port,
                                       String vxToken,
                                       Long eventId,
                                       int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<EventGetResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/event/" + eventId,
                HttpMethod.GET,
                requestEntity,
                EventGetResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        EventGetResponse response = responseEntity.getBody();
        return response;
    }

    public static EventSearchResponse search(TestRestTemplate restTemplate,
                                             int port,
                                             String vxToken,
                                             List<VxIntegrationId> vxIntegrationIdList,
                                             List<VxEvent.State> stateList,
                                             List<VxEvent.Type> typeList,
                                             int offset,
                                             int limit,
                                             int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);


        // Build the URL with query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/event")
                .queryParam("offset", offset)
                .queryParam("limit", limit);

        if (!typeList.isEmpty()){
            builder.queryParam("typeList", typeList);
        }
        if (!stateList.isEmpty()){
            builder.queryParam("stateList", stateList);
        }


        // Make the GET request to /ping/whoAmI
        ResponseEntity<EventSearchResponse> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                EventSearchResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        EventSearchResponse response = responseEntity.getBody();
        return response;
    }
}
