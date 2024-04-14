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
public class StripeOnboardingIntegrationTest {


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


        System.out.println("StripeConfig url: "+secondConfig.url);
        System.out.println("StripeConfig id: " + secondConfig.stripeAccountId);


        System.out.println("User phone: 498 597 618");
        System.out.println("User website: https://www.linkedin.com/in/bogdan-oloeriu");

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
        Assertions.assertEquals(false, account.getPayoutsEnabled());

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
    public void test06SelectAnAccountForPayoutsLetsReview() throws StripeException {
        //https://connect.stripe.com/setup/e/acct_1OLNwlPnbmI8IHhZ/NyAr3g3jvcUH
        // 498 597 618
        // https://www.linkedin.com/in/bogdan-oloeriu
        String activeStripeAccountId = "acct_1OLNwlPnbmI8IHhZ";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();
        Set<String> currentlyDueSet = new HashSet<>(currentlyDueList);


        List<String> expectedList = Arrays.asList("tos_acceptance.ip", "tos_acceptance.date");

        Set<String> exptectedSet = new HashSet<>(expectedList);

        Assertions.assertEquals(exptectedSet, currentlyDueSet);
    }

    @Test
    public void test07ConfigurationComplete() throws StripeException {
        // https://connect.stripe.com/setup/e/acct_1OLO6iPVZaH0vuBA/ue4jMoY4TFXQ
        // 498 597 618
        // https://www.linkedin.com/in/bogdan-oloeriu/
        String activeStripeAccountId = "acct_1OLO6iPVZaH0vuBA";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();

        Assertions.assertTrue(currentlyDueList.isEmpty());
    }

    @Test
    public void test08CycleAllStages() throws StripeException {
        // https://connect.stripe.com/setup/e/acct_1OLONyPnb2T4BnUb/eTWNOXX96TTF
        // 498 597 618
        // https://www.linkedin.com/in/bogdan-oloeriu/
        String activeStripeAccountId = "acct_1OLONyPnb2T4BnUb";
        System.out.println(stripeDevSecretKey);

        Stripe.apiKey = stripeDevSecretKey;
        Account account = Account.retrieve(activeStripeAccountId);

        AccountLink accountLink = VxStripeUtil.createAccountLink(stripeDevSecretKey, activeStripeAccountId,
                systemService.getStripeRefreshRedirectUrl());
        System.out.println("AccountLinkUrl = " + accountLink.getUrl());


        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();

        Assertions.assertTrue(currentlyDueList.isEmpty());
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


    }


}
