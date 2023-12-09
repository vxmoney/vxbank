package eu.vxbank.api.stripe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.StripeConfigHelper;
import eu.vxbank.api.helpers.UserHelper;
import eu.vxbank.api.testutils.SwapTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StripeOnboardingIntegrationTest {


    private static final String testPassword = "secured-test-password";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

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
        String idToken = SwapTokenUtil.swapOnLocalhostCustomTokenForIdToken(customToken);

        return idToken;
    }

    @Test
    public void test01InitiateStripeConfig() throws FirebaseAuthException, JsonProcessingException {
        VxUser userParams = createUserParams();

        String firebaseIdToken = createFirebaseIdToken(userParams.email);

        Assertions.assertNotNull(firebaseIdToken);

        LoginResponse loginResponse = UserHelper.login(firebaseIdToken, restTemplate, port);
        Assertions.assertNotNull(loginResponse);

        //stripeConfig/getByUserId/{userId}
        StripeConfigGetByUserIdResponse configResponse = StripeConfigHelper.getByUserId(loginResponse.id,
                loginResponse.vxToken,
                restTemplate,
                port,
                200);

        Assertions.assertNotNull(configResponse);
        // check that stripe state is notConfigured
        Assertions.assertEquals(VxStripeConfig.State.notConfigured, configResponse.state);

        // stripeConfig/initiateConfig
        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = loginResponse.id;
        StripeConfigInitiateConfigResponse firstConfig = StripeConfigHelper.initiateConfig(loginResponse.vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        // second config should create new link
        StripeConfigInitiateConfigResponse secondConfig = StripeConfigHelper.initiateConfig(loginResponse.vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        Assertions.assertNotEquals(firstConfig.url, secondConfig.url);
    }

    @Test
    public void test02GetConnectedStripeAccountActiveState() throws StripeException {
        String activeStripeAccountId = "acct_1OLMCMPdnG6HZQi4";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();

        Assertions.assertNotNull(account);
        Assertions.assertTrue(currentlyDueList.isEmpty());
    }

}
