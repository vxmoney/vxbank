package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.publicevent.product.dto.ProductCreateParams;
import eu.vxbank.api.endpoints.publicevent.product.dto.ProductSearchResponse;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointParams;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointResponse;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointSearchResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;

public class PublicEventSellingPointHelper {
    public static SellingPointResponse create(TestRestTemplate restTemplate,
                                              int port,
                                              String vxToken,
                                              SellingPointParams params,
                                              int expectedStatusCode) {

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<SellingPointParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<SellingPointResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventSellingPoint", HttpMethod.POST, requestEntity, SellingPointResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        SellingPointResponse response = responseEntity.getBody();
        return response;
    }

    public static SellingPointResponse get(TestRestTemplate restTemplate,
                                           int port,
                                           String vxToken,
                                           Long pointId,
                                           int expectedStatusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<SellingPointResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventSellingPoint/" + pointId,
                HttpMethod.GET,
                requestEntity,
                SellingPointResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        SellingPointResponse response = responseEntity.getBody();
        return response;
    }

    public static SellingPointResponse update(TestRestTemplate restTemplate,
                                              int port,
                                              String vxToken,
                                              Long pointId,
                                              SellingPointParams params,
                                              int expectedStatusCode) {

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<SellingPointParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<SellingPointResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventSellingPoint/" + pointId,
                HttpMethod.PUT, requestEntity, SellingPointResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        SellingPointResponse response = responseEntity.getBody();
        return response;

    }

    public static SellingPointSearchResponse search(TestRestTemplate restTemplate,
                                                    int port,
                                                    String vxToken,
                                                    Long publicEventId,
                                                    int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);


        // Build the URL with query parameters
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/publicEventSellingPoint")
                        .queryParam("publicEventId", publicEventId);


        // Make the GET request
        ResponseEntity<SellingPointSearchResponse> responseEntity = restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                SellingPointSearchResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        SellingPointSearchResponse response = responseEntity.getBody();
        return response;

    }
}
