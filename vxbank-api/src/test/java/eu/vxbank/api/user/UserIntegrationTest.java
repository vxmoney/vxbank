package eu.vxbank.api.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import eu.vxbank.api.endpoints.ping.dto.FirebaseSwapResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginParams;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.endpoints.user.dto.UserStripeLinkResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.helpers.RandomUtil;
import eu.vxbank.api.helpers.StripeConfigHelper;
import eu.vxbank.api.helpers.UserHelper;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.testutils.UserUtils;
import eu.vxbank.api.utils.components.SystemService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    private static final String testPassword = "secured-test-password";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    SystemService systemService;

    private VxUser createUserParams() {
        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("%s@mail.com", uuid);
        return vxUser;
    }

    private String createFirebaseIdToken(String email) throws FirebaseAuthException, JsonProcessingException {

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
                .setDisplayName("name_" + email)
                .setEmailVerified(true)
                .setPassword(testPassword); // Set a secure password for the user

        UserRecord userRecord = firebaseAuth.createUser(request);

        String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
        String idToken = swapOnLocalhostCustomTokenForIdToken(customToken);

        return idToken;
    }

    private String swapOnLocalhostCustomTokenForIdToken(String customToken) throws JsonProcessingException {

        HttpResponse<String> response = Unirest.post("http://localhost:9099/identitytoolkit.googleapis" +
                        ".com/v1/accounts:signInWithCustomToken?key=AIzaSyD3WNQhta9K8SW42PpGDnozZwm16vTJq8")
                .header("Content-Type", "application/json")
                .body("{\"token\":\"" + customToken + "\",\"returnSecureToken\":true}")
                .asString();

        String body = response.getBody();
        System.out.println(body);

        ObjectMapper objectMapper = new ObjectMapper();
        FirebaseSwapResponse oauthResponse = objectMapper.readValue(body, FirebaseSwapResponse.class);
        return oauthResponse.idToken;

    }


    @Test
    public void logInTest() throws FirebaseAuthException, JsonProcessingException {


        VxUser userParams = createUserParams();

        String firebaseIdToken = createFirebaseIdToken(userParams.email);

        Assertions.assertNotNull(firebaseIdToken);

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

        // Example assertion (you should replace this with your actual assertions)
        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals(userParams.email, loginResponse.email);
        Assertions.assertNotNull(loginResponse.name);


        // ping who am I
        LoginResponse pingResponse = pingWhoAmI(loginResponse.vxToken);
        Assertions.assertEquals(loginResponse.email, pingResponse.email);

        // refresh token
        LoginResponse refreshResponse = UserUtils.refreshVxToken(restTemplate, port, loginResponse.vxToken, 200);
        Assertions.assertNotNull(refreshResponse.vxTokenExpiresAt);

    }

    private LoginResponse pingWhoAmI(String vxToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/ping/whoAmI", HttpMethod.GET, requestEntity, LoginResponse.class);

        // Extract the response
        LoginResponse responseBody = responseEntity.getBody();
        return responseBody;
    }

    private LoginResponse setupSideUser(String stripeId) throws FirebaseAuthException, JsonProcessingException {


        String email = RandomUtil.generateRandomEmail();
        String vxToken = UserHelper.generateVxToken(email, restTemplate, port);

        LoginResponse loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        Assertions.assertEquals(email, loginResponse.email);

        VxUser vxUser = new VxUser();
        vxUser.id = loginResponse.id;
        vxUser.email = email;
        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = vxUser.id;
        StripeConfigInitiateConfigResponse initiateConfigResponse = StripeConfigHelper.initiateConfig(vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        Long vxUserId = vxUser.id;

        VxBankDatastore ds = systemService.getVxBankDatastore();
        SideStripeConfigHelper.setStripeAccountId(ds, vxUserId, stripeId);

        loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        loginResponse.vxToken = vxToken;

        return loginResponse;

    }

    @Test
    public void getStripeLoginLinkTest() throws FirebaseAuthException, JsonProcessingException {
        LoginResponse loginResponse = setupSideUser("acct_1OgqHAB36QPiP0qI");
        UserStripeLinkResponse link = UserUtils.getStripeLoginLink(restTemplate, port, loginResponse.vxToken, 200);
        Assertions.assertNotNull(link);
        Assertions.assertNotNull(link.uri);
        System.out.println("Stripe login link: "+ link.uri);
    }

}
