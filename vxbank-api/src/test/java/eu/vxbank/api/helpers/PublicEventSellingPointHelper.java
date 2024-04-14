package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointCreateParams;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointCreateResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PublicEventSellingPointHelper {
    public static SellingPointCreateResponse create(TestRestTemplate restTemplate,
                                                    int port,
                                                    String vxToken,
                                                    SellingPointCreateParams params,
                                                    int expectedStatusCode) {

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<SellingPointCreateParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<SellingPointCreateResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventSellingPoint", HttpMethod.POST, requestEntity, SellingPointCreateResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        SellingPointCreateResponse response = responseEntity.getBody();
        return response;
    }
}
