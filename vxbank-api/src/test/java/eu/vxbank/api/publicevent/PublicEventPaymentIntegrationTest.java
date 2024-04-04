package eu.vxbank.api.publicevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import com.stripe.net.Webhook;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentParams;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentResponse;
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
public class PublicEventPaymentIntegrationTest {

    // <standard test fields>

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    // </standard test fields>

    private class Setup {
        Long userId;
        String vxToken;
        String stripeAccountId;
        String email;
        Long publicEventId;
        Long vxPublicEventClientId;

    }

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

    void depositFunds(String vxToken, Long publicEventId, Long vxPublicEventClientId, Long value) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        // client deposit funds

        PublicEventClientDepositFundsParams depositFundsParams = PublicEventClientDepositFundsParams.builder()
                .value(value)
                .build();
        PublicEventClientDepositFundsResponse depositFundsResponse = PublicEventHelper.clientDepositFunds(restTemplate,
                port,
                vxToken,
                publicEventId,
                depositFundsParams,
                200);
        Assertions.assertNotNull(depositFundsResponse);
        Assertions.assertEquals(vxPublicEventClientId, depositFundsResponse.vxPublicEventClientId);
        Assertions.assertEquals(publicEventId, depositFundsResponse.vxPublicEventId);
        Assertions.assertNotNull(depositFundsResponse.vxEventPaymentId);
        Assertions.assertNotNull(depositFundsResponse.stripeSessionPaymentUrl);
        Assertions.assertNotNull(depositFundsResponse.stripeSessionId);

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

    @Test
    public void testClientPayments() throws StripeException, FirebaseAuthException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        Setup manager = setupUserAndEvent("acct_1P05koBBqbt0qcrd");
        Setup client = setupClientAndJoinEvent(manager.publicEventId);

        Long value = 1000L;
        depositFunds(client.vxToken, client.publicEventId, client.vxPublicEventClientId, value);

        // check report
        PublicEventClientPaymentReportResponse clientReport = PublicEventClientPaymentHelper.clientPaymentReport(restTemplate,
                port,
                client.vxToken,
                client.publicEventId,
                client.vxPublicEventClientId,
                200);
        Assertions.assertEquals(value, clientReport.availableBalance);


        ManagerRegistersPaymentResponse response = PublicEventClientPaymentHelper.managerRegistersPayment(
                restTemplate,
                port,
                manager.vxToken,
                ManagerRegistersPaymentParams.builder()
                        .eventId(client.publicEventId)
                        .clientId(client.vxPublicEventClientId)
                        .value(250L)
                        .build(),
                200);

        clientReport = PublicEventClientPaymentHelper.clientPaymentReport(restTemplate,
                port,
                client.vxToken,
                client.publicEventId,
                client.vxPublicEventClientId,
                200);
        Assertions.assertEquals(value, clientReport.availableBalance);

        System.out.println("end of test");
    }
}
