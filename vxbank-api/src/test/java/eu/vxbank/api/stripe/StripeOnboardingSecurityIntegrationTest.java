package eu.vxbank.api.stripe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.helpers.StripeConfigHelper;
import eu.vxbank.api.helpers.UserHelper;
import eu.vxbank.api.testutils.SwapTokenUtil;
import eu.vxbank.api.utils.components.SystemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxService;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StripeOnboardingSecurityIntegrationTest {


    private static final String testPassword = "secured-test-password";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

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
    public void test9FinalizeConfig() throws StripeException, FirebaseAuthException, JsonProcessingException {

        VxBankDatastore ds = systemService.getVxBankDatastore();

        // set the user
        Long userId = 1L;
        String stripeAccountId = "acct_1OLOsQB0moZ0HQUD";
        String coreMail = UUID.randomUUID()
                .toString();
        String email = String.format("$%s@mail.com", coreMail);
        VxUser vxUserParams = VxUser.builder()
                .id(userId)
                .email(email)
                .build();
        VxUser vxUser = VxService.persist(vxUserParams, ds, VxUser.class);
        Assertions.assertEquals(vxUserParams.id, vxUser.id);

        // set stripeConfig
        Long configId = 2L;
        VxStripeConfig configParams = VxStripeConfig.builder()
                .id(configId)
                .userId(vxUser.id)
                .stripeAccountId(stripeAccountId)
                .state(VxStripeConfig.State.configurationInProgress)
                .build();
        VxService.persist(configParams, ds, VxStripeConfig.class);
        List<VxStripeConfig> updatedList = VxService.getByUserId(vxUser.id, new HashMap<>(), ds, VxStripeConfig.class);
        VxStripeConfig stripeConfig = updatedList.get(0);
        Assertions.assertEquals(stripeAccountId, stripeConfig.stripeAccountId);

        // generate firebaseCustomToken for this user
        String firebaseToken = createFirebaseIdToken(vxUser.email);

        // login
        LoginResponse loginResponse = UserHelper.login(firebaseToken, restTemplate, port);
        String vxToken = loginResponse.vxToken;
        Assertions.assertEquals(vxUser.id, loginResponse.id);
        Assertions.assertNotNull(vxToken);

        // initiate config
        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = loginResponse.id;

        // this is already configured, so we should expect conflict 409
        // expect CONFLICT(409, Series.CLIENT_ERROR, "Conflict"),
        StripeConfigHelper.initiateConfig(loginResponse.vxToken, initiateConfigParams, restTemplate, port, 409);

        // finalizeConfig
        StripeConfigHelper.finalizeConfig(loginResponse.vxToken, initiateConfigParams, restTemplate, port, 200);

        // pingWhoAmI
        LoginResponse pingResponse = PingHelper.whoAmI(loginResponse.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(VxStripeConfig.State.active, pingResponse.stripeConfigState);


    }

    // we might use these ids' as admins at some point in the future
    // complete cycle user A: acct_1OLVFTBDXgpnX6Hr
    // complete cycle suer B: acct_1OLVIoPaNl3jOqeD
    private LoginResponse generateRandomLoggedInUser() throws FirebaseAuthException, JsonProcessingException {
        VxBankDatastore ds = systemService.getVxBankDatastore();
        String coreMail = UUID.randomUUID()
                .toString();
        String email = String.format("$%s@mail.com", coreMail);
        VxUser vxUser = VxUser.builder()
                .email(email)
                .build();
        VxService.persist(vxUser, ds, VxUser.class);
        Assertions.assertNotNull(vxUser.id);

        String vxToken = UserHelper.generateVxToken(vxUser.email, restTemplate, port);
        Assertions.assertNotNull(vxToken);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = vxUser.id;
        loginResponse.email = vxUser.email;
        loginResponse.vxToken = vxToken;

        return loginResponse;
    }


    @Test
    public void test00InitiateConfigByDifferentUser() throws
            StripeException,
            FirebaseAuthException,
            JsonProcessingException {

        LoginResponse loginA = generateRandomLoggedInUser();
        LoginResponse loginB = generateRandomLoggedInUser();

        StripeConfigInitiateConfigParams configAParams = new StripeConfigInitiateConfigParams();
        configAParams.userId = loginA.id;

        StripeConfigInitiateConfigParams configBParams = new StripeConfigInitiateConfigParams();
        configBParams.userId = loginB.id;


        // userA tries to initiate userB stripe config
        StripeConfigHelper.initiateConfig(loginA.vxToken,
                configBParams,
                restTemplate,
                port,
                403);




    }


}
