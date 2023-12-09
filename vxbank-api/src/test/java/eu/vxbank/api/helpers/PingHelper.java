package eu.vxbank.api.helpers;

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
}
