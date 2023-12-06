package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.user.dto.LoginParams;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class UserEndpointHelper {
    public static LoginResponse login(String firebaseIdToken, TestRestTemplate restTemplate, int port){

        LoginParams loginParams = new LoginParams();
        loginParams.firebaseIdToken = firebaseIdToken;

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<LoginParams> requestEntity = new HttpEntity<>(loginParams, headers);

        // Make the POST request
        ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/user/login",
                HttpMethod.POST,
                requestEntity,
                LoginResponse.class
        );

        // Extract the response
        LoginResponse loginResponse = responseEntity.getBody();
        return loginResponse;
    }
}
