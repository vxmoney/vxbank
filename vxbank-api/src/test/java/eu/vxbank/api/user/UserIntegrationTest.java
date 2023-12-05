package eu.vxbank.api.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import eu.vxbank.api.endpoints.ping.dto.FirebaseSwapResponse;
import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxService;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    private static  final String testPassword = "secured-test-password";
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

        try {
            FirebaseApp.initializeApp();
        } catch (Exception e) {
            System.out.println("Firebase was already initialized");
            // no need to initialize again
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
                .setEmailVerified(true)
                .setPassword(testPassword); // Set a secure password for the user

        UserRecord userRecord = firebaseAuth.createUser(request);
        String userId = userRecord.getUid();
        System.out.println("userId=" + userId);

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
    }




}
