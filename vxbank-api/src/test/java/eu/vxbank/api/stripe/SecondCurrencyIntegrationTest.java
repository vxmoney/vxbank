package eu.vxbank.api.stripe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateSpecificCurrencyParams;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.helpers.StripeConfigHelper;
import eu.vxbank.api.helpers.UserHelper;
import eu.vxbank.api.testutils.SwapTokenUtil;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
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
import vxbank.datastore.data.service.VxDsService;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecondCurrencyIntegrationTest {


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

        if (FirebaseApp.getApps().isEmpty()) {
            // Firebase has not been initialized yet, so initialize it
            FirebaseApp.initializeApp();
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

    class Setup{
        public Long userId;
        public String stripeAccountId;
        public String coreMail;
        public String email;
        public VxUser vxUser;
        public String vxToken;
    }

    private Setup buildSetup() throws FirebaseAuthException, JsonProcessingException {
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
        VxUser vxUser = VxDsService.persist(vxUserParams, ds, VxUser.class);
        Assertions.assertEquals(vxUserParams.id, vxUser.id);

        // set stripeConfig
        Long configId = 2L;
        VxStripeConfig configParams = VxStripeConfig.builder()
                .id(configId)
                .userId(vxUser.id)
                .stripeAccountId(stripeAccountId)
                .state(VxStripeConfig.State.configurationInProgress)
                .build();
        VxDsService.persist(configParams, ds, VxStripeConfig.class);
        List<VxStripeConfig> updatedList = VxDsService.getByUserId(vxUser.id, new HashMap<>(), ds, VxStripeConfig.class);
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

        // finalize config
        StripeConfigHelper.finalizeConfig(loginResponse.vxToken, initiateConfigParams, restTemplate, port, 200);

        // pingWhoAmI
        LoginResponse pingResponse = PingHelper.whoAmI(loginResponse.vxToken, restTemplate,port,200);
        Assertions.assertEquals(VxStripeConfig.State.active, pingResponse.stripeConfigState);

        Setup setup = new Setup();
        setup.userId = userId;
        setup.stripeAccountId = stripeAccountId;
        setup.coreMail = coreMail;
        setup.email = email;
        setup.vxUser = vxUser;
        setup.vxToken = vxToken;

        return setup;
    }




}
