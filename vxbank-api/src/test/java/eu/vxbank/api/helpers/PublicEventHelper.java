package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.event.dto.EventGetResponse;
import eu.vxbank.api.endpoints.event.dto.EventSearchResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

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

    public static PublicEventGetResponse get(TestRestTemplate restTemplate,
                                             int port,
                                             String vxToken,
                                             Long eventId,
                                             int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<PublicEventGetResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEvent/" + eventId,
                HttpMethod.GET,
                requestEntity,
                PublicEventGetResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        PublicEventGetResponse response = responseEntity.getBody();
        return response;
    }

    public static PublicEventSearchResponse search(TestRestTemplate restTemplate,
                                                   int port,
                                                   String vxToken,
                                                   Long vxUserId,
                                                   int expectedStatusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);


        // Build the URL with query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/publicEvent")
                .queryParam("vxUserId", vxUserId);

        // Make the GET request
        ResponseEntity<PublicEventSearchResponse> responseEntity = restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                PublicEventSearchResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        PublicEventSearchResponse response = responseEntity.getBody();
        return response;
    }

    public static PublicEventAddManagerResponse addManager(TestRestTemplate restTemplate,
                                                           int port,
                                                           String vxToken,
                                                           PublicEventAddMangerParams params,
                                                           int expectedStatusCode) {
        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<PublicEventAddMangerParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<PublicEventAddManagerResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEvent/" + params.publicEventId + "/managers",
                HttpMethod.POST, requestEntity, PublicEventAddManagerResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        PublicEventAddManagerResponse response = responseEntity.getBody();
        return response;
    }

    public static PublicEventGetManagerListResponse getManagers(TestRestTemplate restTemplate,
                                                                int port,
                                                                String vxToken,
                                                                Long publicEventId,
                                                                int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<PublicEventGetManagerListResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEvent/" + publicEventId + "/managers",
                HttpMethod.GET,
                requestEntity,
                PublicEventGetManagerListResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        PublicEventGetManagerListResponse response = responseEntity.getBody();
        return response;

    }

    public static String deleteManager(TestRestTemplate restTemplate,
                                     int port,
                                     String vxToken,
                                     Long publicEventId,
                                     String mail,
                                     int expectedStatusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEvent/" + publicEventId + "/managers/" + mail,
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        String response = responseEntity.getBody();
        return response;
    }

    public static PublicEventCheckRegisterClientResponse checkRegisterClient(TestRestTemplate restTemplate,
                                                                             int port,
                                                                             String vxToken,
                                                                             Long publicEventId,
                                                                             int expectedStatusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<PublicEventCheckRegisterClientResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEvent/" + publicEventId + "/checkRegisterClient",
                HttpMethod.GET,
                requestEntity,
                PublicEventCheckRegisterClientResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        PublicEventCheckRegisterClientResponse response = responseEntity.getBody();
        return response;
    }

    public static PublicEventClientDepositFundsResponse clientDepositFunds(TestRestTemplate restTemplate,
                                                                           int port,
                                                                           String vxToken,
                                                                           Long publicEventId,
                                                                           PublicEventClientDepositFundsParams depositFundsParams,
                                                                           int expectedStatusCode) {
        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the request body and headers
        HttpEntity<PublicEventClientDepositFundsParams> requestEntity = new HttpEntity<>(depositFundsParams, headers);

        // Make the POST request
        ResponseEntity<PublicEventClientDepositFundsResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEvent/" + publicEventId + "/clientDepositFunds",
                HttpMethod.POST, requestEntity, PublicEventClientDepositFundsResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        PublicEventClientDepositFundsResponse response = responseEntity.getBody();
        return response;
    }
}
