package eu.vxbank.api.testutils;

import eu.vxbank.api.endpoints.event.dto.EventGetResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class UserUtils {
    public static LoginResponse refreshVxToken(TestRestTemplate restTemplate,
                                       int port,
                                       String vxToken,
                                       int expectedStatusCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/user/refreshVxToken",
                HttpMethod.GET,
                requestEntity,
                LoginResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        LoginResponse response = responseEntity.getBody();
        return response;
    }
}
