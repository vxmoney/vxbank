package eu.vxbank.api.publicevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import com.stripe.net.Webhook;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.PublicEventClientPaymentReportResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.*;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.*;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicEventIntegrationTest {

    private class Setup {
        Long userId;
        String vxToken;
        String stripeAccountId;
        String email;
        Long publicEventId;
        Long vxPublicEventClientId;

    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    private Setup setupUser(String stripeAccountId) throws
            FirebaseAuthException,
            JsonProcessingException,
            StripeException {


        Setup setup = new Setup();

        setup.email = RandomUtil.generateRandomEmail();
        setup.vxToken = UserHelper.generateVxToken(setup.email, restTemplate, port);
        setup.stripeAccountId = stripeAccountId;


        LoginResponse loginResponse = PingHelper.whoAmI(setup.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(setup.email, loginResponse.email);


        setup.userId = loginResponse.id;

        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = setup.userId;
        StripeConfigInitiateConfigResponse initiateConfigResponse = StripeConfigHelper.initiateConfig(setup.vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        VxBankDatastore ds = systemService.getVxBankDatastore();
        SideStripeConfigHelper.setStripeAccountId(ds, setup.userId, setup.stripeAccountId);

        Optional<VxUser> vxUser = VxDsService.getUserByEmail(setup.email, ds);
        Assertions.assertTrue(vxUser.isPresent());

        return setup;
    }

    private Setup setupClient() throws
            FirebaseAuthException,
            JsonProcessingException,
            StripeException {


        Setup setup = new Setup();

        setup.email = RandomUtil.generateRandomEmail();
        setup.vxToken = UserHelper.generateVxToken(setup.email, restTemplate, port);


        LoginResponse loginResponse = PingHelper.whoAmI(setup.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(setup.email, loginResponse.email);


        setup.userId = loginResponse.id;


        VxBankDatastore ds = systemService.getVxBankDatastore();

        Optional<VxUser> vxUser = VxDsService.getUserByEmail(setup.email, ds);
        Assertions.assertTrue(vxUser.isPresent());

        return setup;
    }

    private Setup setupNotConfiguredStripeUser() throws FirebaseAuthException, JsonProcessingException {
        Setup setup = new Setup();

        setup.email = RandomUtil.generateRandomEmail();
        setup.vxToken = UserHelper.generateVxToken(setup.email, restTemplate, port);
        LoginResponse loginResponse = PingHelper.whoAmI(setup.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(setup.email, loginResponse.email);
        return setup;
    }


    @Test
    public void createFailIfNotConfiguredTest000() throws FirebaseAuthException, JsonProcessingException {
        Setup setup = setupNotConfiguredStripeUser();

        Long timeStamp = new Date().getTime();
        String title = "Event - " + timeStamp;
        PublicEventCreateParams params = PublicEventCreateParams.builder()
                .vxUserId(setup.userId)
                .vxIntegrationId(VxIntegrationId.vxEvents)
                .title(title)
                .currency("eur")
                .build();

        PublicEventCreateResponse publicEventCreateResponse = PublicEventHelper.create(restTemplate,
                port,
                setup.vxToken,
                params,
                500);
    }

    @Test
    public void createGetTest001() throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup setup = setupUser("acct_1OO0j2PVTA3jVN7Z");
        Long timeStamp = new Date().getTime();
        String title = "Event - " + timeStamp;
        PublicEventCreateParams params = PublicEventCreateParams.builder()
                .vxUserId(setup.userId)
                .vxIntegrationId(VxIntegrationId.vxEvents)
                .title(title)
                .currency("eur")
                .build();

        PublicEventCreateResponse publicEventCreateResponse = PublicEventHelper.create(restTemplate,
                port,
                setup.vxToken,
                params,
                200);

        Assertions.assertNotNull(publicEventCreateResponse.id);
        Assertions.assertEquals(setup.userId, publicEventCreateResponse.vxUserId);
        Assertions.assertNotNull(publicEventCreateResponse.managerIdList);

        PublicEventGetResponse getResponse = PublicEventHelper.get(restTemplate, port,
                setup.vxToken, publicEventCreateResponse.id, 200);
        Assertions.assertEquals(publicEventCreateResponse.id, getResponse.id);
        Assertions.assertTrue(getResponse.managerIdList.contains(setup.userId));
    }

    public Setup setupUserAndEvent(String stripeAccountId) throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup setup = setupUser(stripeAccountId);
        Long timeStamp = new Date().getTime();
        String title = "Event - " + timeStamp;
        PublicEventCreateParams params = PublicEventCreateParams.builder()
                .vxUserId(setup.userId)
                .vxIntegrationId(VxIntegrationId.vxEvents)
                .title(title)
                .currency("eur")
                .build();

        PublicEventCreateResponse publicEventCreateResponse = PublicEventHelper.create(restTemplate,
                port,
                setup.vxToken,
                params,
                200);
        setup.publicEventId = publicEventCreateResponse.id;
        return setup;
    }

    @Test
    public void testSearch() throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup setup = setupUser("acct_1OO0j2PVTA3jVN7Z");
        Long timeStamp = new Date().getTime();
        String title = "Event - " + timeStamp;

        PublicEventCreateParams params = PublicEventCreateParams.builder()
                .vxUserId(setup.userId)
                .vxIntegrationId(VxIntegrationId.vxEvents)
                .title(title)
                .currency("eur")
                .build();

        PublicEventHelper.create(restTemplate,
                port,
                setup.vxToken,
                params,
                200);
        PublicEventHelper.create(restTemplate,
                port,
                setup.vxToken,
                params,
                200);


        PublicEventSearchResponse publicEventSearchResponse =
                PublicEventHelper.search(
                        restTemplate,
                        port,
                        setup.vxToken,
                        setup.userId,
                        200);
        Assertions.assertEquals(2, publicEventSearchResponse.eventList.size());
        Assertions.assertTrue(publicEventSearchResponse.eventList.stream().anyMatch(e -> e.title.equals(title)));
    }

    @Test
    public void testAddManager() throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup setupA = setupUserAndEvent("acct_1P05koBBqbt0qcrd");
        Setup setupB = setupUser("acct_1OO0j2PVTA3jVN7Z");


        //add fake manager
        String fakeEmail = RandomUtil.generateRandomEmail();
        PublicEventAddMangerParams fakeParams = PublicEventAddMangerParams.builder()
                .publicEventId(setupA.publicEventId)
                .email(fakeEmail)
                .build();

        PublicEventHelper.addManager(restTemplate,
                port,
                setupA.vxToken,
                fakeParams,
                500);

        Optional<VxUser> optionalUserB = VxDsService.getUserByEmail(setupB.email, systemService.getVxBankDatastore());
        Assertions.assertTrue(optionalUserB.isPresent());

        // add manager
        PublicEventAddMangerParams goodParams = PublicEventAddMangerParams.builder()
                .publicEventId(setupA.publicEventId)
                .email(setupB.email)
                .build();
        PublicEventAddManagerResponse addManagerResponse = PublicEventHelper.addManager(restTemplate,
                port,
                setupA.vxToken,
                goodParams,
                200);
        Assertions.assertNotNull(addManagerResponse);
        Assertions.assertEquals(setupB.userId, addManagerResponse.userId);

        // add manager again
        PublicEventHelper.addManager(restTemplate,
                port,
                setupA.vxToken,
                goodParams,
                500);

        // get mangers and check is in the list
        PublicEventGetManagerListResponse managerListResponse = PublicEventHelper.getManagers(restTemplate,
                port,
                setupA.vxToken,
                setupA.publicEventId,
                200);
        Assertions.assertTrue(managerListResponse.managerList.stream().anyMatch(m -> m.id.equals(setupB.userId)));

        // delete manger
        String deleteManagerResponse = PublicEventHelper.deleteManager(restTemplate,
                port,
                setupA.vxToken,
                setupA.publicEventId,
                setupB.email,
                200);

        // check manager is not in the list
        managerListResponse = PublicEventHelper.getManagers(restTemplate,
                port,
                setupA.vxToken,
                setupA.publicEventId,
                200);
        Assertions.assertFalse(managerListResponse.managerList.stream().anyMatch(m -> m.id.equals(setupB.userId)));
    }

    @Test
    public void testCheckRegisterClient() throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup setup = setupUserAndEvent("acct_1P05koBBqbt0qcrd");
        Setup client = setupClient();

        PublicEventCheckRegisterClientResponse checkRegisterClientResponse = PublicEventHelper.checkRegisterClient(restTemplate,
                port,
                client.vxToken,
                setup.publicEventId,
                200);
        Assertions.assertNotNull(checkRegisterClientResponse);
        Assertions.assertEquals(client.userId, checkRegisterClientResponse.userId);
        Assertions.assertEquals(setup.publicEventId, checkRegisterClientResponse.publicEventId);

        PublicEventCheckRegisterClientResponse secondRegistration = PublicEventHelper.checkRegisterClient(restTemplate,
                port,
                client.vxToken,
                setup.publicEventId,
                200);
        Assertions.assertEquals(checkRegisterClientResponse.id, secondRegistration.id);


    }

    private Setup setupClientAndJoinEvent(Long publicEventId) throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup client = setupClient();
        PublicEventCheckRegisterClientResponse checkRegisterClientResponse = PublicEventHelper.checkRegisterClient(restTemplate,
                port,
                client.vxToken,
                publicEventId,
                200);
        client.vxPublicEventClientId = checkRegisterClientResponse.id;
        client.publicEventId = publicEventId;
        return client;
    }

    public String loadFileAsString(String fileName) throws IOException {
        // Get the class loader
        ClassLoader classLoader = getClass().getClassLoader();

        // Use the class loader to load the file as a resource
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        } else {
            try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
                // Use Scanner to read the content of the file into a string
                return scanner.useDelimiter("\\A")
                        .next();
            }
        }
    }

    @Test
    public void testClientDepositFunds() throws StripeException, FirebaseAuthException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        Setup setup = setupUserAndEvent("acct_1P05koBBqbt0qcrd");
        Setup client = setupClientAndJoinEvent(setup.publicEventId);

        Assertions.assertEquals(setup.publicEventId, client.publicEventId);
        Assertions.assertNotNull(client.vxPublicEventClientId);
        Assertions.assertNotEquals(client.vxPublicEventClientId, client.userId);

        // client deposit funds
        Long value = 1000L;
        PublicEventClientDepositFundsParams depositFundsParams = PublicEventClientDepositFundsParams.builder()
                .value(value)
                .build();
        PublicEventClientDepositFundsResponse depositFundsResponse = PublicEventHelper.clientDepositFunds(restTemplate,
                port,
                client.vxToken,
                setup.publicEventId,
                depositFundsParams,
                200);
        Assertions.assertNotNull(depositFundsResponse);
        Assertions.assertEquals(client.vxPublicEventClientId, depositFundsResponse.vxPublicEventClientId);
        Assertions.assertEquals(setup.publicEventId, depositFundsResponse.vxPublicEventId);
        Assertions.assertNotNull(depositFundsResponse.vxEventPaymentId);
        Assertions.assertNotNull(depositFundsResponse.stripeSessionPaymentUrl);
        Assertions.assertNotNull(depositFundsResponse.stripeSessionId);

        System.out.println("Payment url = " + depositFundsResponse.stripeSessionPaymentUrl);
        System.out.println("User card 4000000000000077");

        {
            // check report before webhook hits
            PublicEventClientPaymentReportResponse clientReport = PublicEventClientPaymentHelper.clientPaymentReport(restTemplate,
                    port,
                    client.vxToken,
                    client.publicEventId,
                    client.vxPublicEventClientId,
                    200);
            Assertions.assertNotNull(clientReport);
            Assertions.assertEquals(0L, clientReport.availableBalance);
            Assertions.assertEquals(0, clientReport.clinetPaymentList.size());
        }


        {
            // simulate stripeWebhook
            String fileName = "publicEventIntegrationTest/clientDepositFunds-00.json";
            String fileContent = loadFileAsString(fileName);
            String webhookSigningSecret = "whsec_b36f59fd7556a24cbdd59589110a616aebb7a35167d04d2aade484c8a345af53";
            String body = fileContent.replace("#tagStripeSessionId", depositFundsResponse.stripeSessionId);

            long timeStamp = (new Date()).getTime();
            String payload = timeStamp + "." + body;
            String signedPayload = Webhook.Util.computeHmacSha256(webhookSigningSecret, payload);
            String stripeSignature = "t=" + timeStamp + ",v1=" + signedPayload;

            WebhookHelper.handleStripeWebhook(restTemplate, port, stripeSignature, body, 200);
        }

        {
            // check report after webhook hits
            PublicEventClientPaymentReportResponse clientReport = PublicEventClientPaymentHelper.clientPaymentReport(restTemplate,
                    port,
                    client.vxToken,
                    client.publicEventId,
                    client.vxPublicEventClientId,
                    200);
            Assertions.assertNotNull(clientReport);
            Assertions.assertEquals(value, clientReport.availableBalance);
            Assertions.assertEquals(1, clientReport.clinetPaymentList.size());
        }



    }


}
