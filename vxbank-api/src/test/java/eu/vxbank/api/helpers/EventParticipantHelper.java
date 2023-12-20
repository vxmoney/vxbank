package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventGetResponse;
import eu.vxbank.api.endpoints.eventparticipant.dto.EventParticipantGetByEventIdResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class EventParticipantHelper {

    public static EventParticipantGetByEventIdResponse getByEventId(TestRestTemplate restTemplate,
                                                                    int port,
                                                                    String vxToken,
                                                                    Long eventId,
                                                                    int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<EventParticipantGetByEventIdResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/eventparticipant/getByEventId/" + eventId,
                HttpMethod.GET,
                requestEntity,
                EventParticipantGetByEventIdResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        EventParticipantGetByEventIdResponse response = responseEntity.getBody();
        return response;
    }
}
