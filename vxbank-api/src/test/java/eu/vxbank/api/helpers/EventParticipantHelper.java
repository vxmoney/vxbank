package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventGetResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class EventParticipantHelper {

    public static EventGetResponse getByEventId(TestRestTemplate restTemplate,
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
                "http://localhost:" + port + "/eventparticipant/getByEventId/" + eventId,
                HttpMethod.GET,
                requestEntity,
                EventGetResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        EventGetResponse response = responseEntity.getBody();
        return response;
    }
}
