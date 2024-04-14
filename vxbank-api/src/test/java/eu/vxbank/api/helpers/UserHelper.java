package eu.vxbank.api.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import eu.vxbank.api.endpoints.ping.dto.PingInitiateVxGamingResponse;
import eu.vxbank.api.endpoints.user.dto.LoginParams;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.testutils.SwapTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class UserHelper {
    public static LoginResponse login(String firebaseIdToken, TestRestTemplate restTemplate, int port) {

        LoginParams loginParams = new LoginParams();
        loginParams.firebaseIdToken = firebaseIdToken;

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<LoginParams> requestEntity = new HttpEntity<>(loginParams, headers);

        // Make the POST request
        ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange("http://localhost:" + port + "/user/login",
                HttpMethod.POST,
                requestEntity,
                LoginResponse.class);

        // Extract the response
        LoginResponse loginResponse = responseEntity.getBody();
        return loginResponse;
    }

    private static String createFirebaseIdToken(String email) throws FirebaseAuthException, JsonProcessingException {

        String fireEmulatorEnv = System.getenv("FIREBASE_AUTH_EMULATOR_HOST");
        if (fireEmulatorEnv == null) {
            throw new IllegalStateException("FIREBASE_AUTH_EMULATOR_HOST is not set");
        }

        if (FirebaseApp.getApps().isEmpty()) {
            // Firebase has not been initialized yet, so initialize it
            FirebaseApp.initializeApp();
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
                .setEmailVerified(true)
                .setPassword("testPassword");

        UserRecord userRecord = firebaseAuth.createUser(request);
        String userId = userRecord.getUid();

        String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
        String idToken = SwapTokenUtil.swapOnLocalhostCustomTokenForIdToken(customToken);

        return idToken;
    }

    public static String generateVxToken(String email, TestRestTemplate restTemplate, int port) throws
            FirebaseAuthException,
            JsonProcessingException {

        String firebaseIdToken = createFirebaseIdToken(email);
        LoginResponse loginResponse = UserHelper.login(firebaseIdToken, restTemplate, port);
        String vxToken = loginResponse.vxToken;

        return vxToken;
    }


    public static LoginResponse refreshToken(TestRestTemplate restTemplate,
                                             String vxToken,
                                             int port,
                                             int expectedStatusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/user/refreshVxToken", HttpMethod.GET, requestEntity, LoginResponse.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        LoginResponse responseBody = responseEntity.getBody();
        return responseBody;
    }
}
