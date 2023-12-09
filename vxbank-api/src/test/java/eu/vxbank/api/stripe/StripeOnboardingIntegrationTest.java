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

import java.util.*;

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
    public void test00InitiateStripeConfig() throws FirebaseAuthException, JsonProcessingException {
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
    public void test01GetConnectedStripeAccountActiveState() throws StripeException {
        String activeStripeAccountId = "acct_1OLMCMPdnG6HZQi4";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();

        Assertions.assertNotNull(account);
        Assertions.assertTrue(currentlyDueList.isEmpty());
    }

    @Test
    public void test02OnlyLink() throws StripeException {
        String activeStripeAccountId = "acct_1OLN0IBAJYnco4oS";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();
        Set<String> currentlyDueSet = new HashSet<>(currentlyDueList);

        Set<String> onlyLinkSet = new HashSet<>();
        onlyLinkSet.add("external_account");
        onlyLinkSet.add("tos_acceptance.date");
        onlyLinkSet.add("tos_acceptance.ip");

        Assertions.assertEquals(onlyLinkSet, currentlyDueSet);
    }

    /**
     * LinkVisited -> same as OnlyLink
     * Sms sent -> same as OnlyLink
     * Sms verified -> same as OnlyLink
     * Tell us about your business -> is different
     */
    @Test
    public void test03TellUsAboutYourBusiness() throws StripeException {
        String activeStripeAccountId = "acct_1OLN8hBG8FE1JWAR";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();
        Set<String> currentlyDueSet = new HashSet<>(currentlyDueList);


        List<String> tellUsAboutYourBusinessList = Arrays.asList("individual.address.city",
                "individual.last_name",
                "individual.dob.year",
                "individual.address.line1",
                "individual.email",
                "tos_acceptance.ip",
                "tos_acceptance.date",
                "external_account",
                "individual.phone",
                "individual.address.postal_code",
                "individual.dob.month",
                "individual.first_name",
                "business_profile.mcc",
                "individual.dob.day",
                "business_profile.url");

        Set<String> tellUsBoutYourBusinessSet = new HashSet<>(tellUsAboutYourBusinessList);

        Assertions.assertEquals(tellUsBoutYourBusinessSet, currentlyDueSet);
    }


    @Test
    public void test04TellUsAboutHowYouEarnMoney() throws StripeException {
        String activeStripeAccountId = "acct_1OLNT0BCbS7cY6AX";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();
        Set<String> currentlyDueSet = new HashSet<>(currentlyDueList);


        List<String> expectedList = Arrays.asList("tos_acceptance.ip",
                "tos_acceptance.date",
                "external_account",
                "business_profile.mcc",
                "business_profile.url");

        Set<String> exptectedSet = new HashSet<>(expectedList);

        Assertions.assertEquals(exptectedSet, currentlyDueSet);
    }

    @Test
    public void test05SelectAnAccountForPayouts() throws StripeException {
        // 498 597 618
        // https://www.linkedin.com/in/bogdan-oloeriu/
        String activeStripeAccountId = "acct_1OLNlQBOUhUI9DOu";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();
        Set<String> currentlyDueSet = new HashSet<>(currentlyDueList);


        List<String> expectedList = Arrays.asList("tos_acceptance.ip", "tos_acceptance.date", "external_account");

        Set<String> exptectedSet = new HashSet<>(expectedList);

        Assertions.assertEquals(exptectedSet, currentlyDueSet);
    }

    @Test
    public void test06() throws StripeException {
        // 498 597 618
        // https://www.linkedin.com/in/bogdan-oloeriu/
        String activeStripeAccountId = "fake";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();
        Set<String> currentlyDueSet = new HashSet<>(currentlyDueList);


        List<String> expectedList = Arrays.asList("tos_acceptance.ip", "tos_acceptance.date", "external_account");

        Set<String> exptectedSet = new HashSet<>(expectedList);

        Assertions.assertEquals(exptectedSet, currentlyDueSet);
    }

}
