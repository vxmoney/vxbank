package eu.vxbank.api.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import com.stripe.net.Webhook;
import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventPayCreateResponse;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxGame;
import vxbank.datastore.data.models.VxUser;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)


public class PayEventIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    Map<Long, String> tokenMap = new HashMap<>();

    private VxUser setupFullUser(String stripeAccountIdUserA) throws
            FirebaseAuthException,
            JsonProcessingException,
            StripeException {

        // stripe id: acct_1OO0j2PVTA3jVN7Z


        String email = RandomUtil.generateRandomEmail();
        String vxToken = UserHelper.generateVxToken(email, restTemplate, port);
        String vxTokenUserA = vxToken;


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
        SideStripeConfigHelper.setStripeAccountId(ds, vxUserId, stripeAccountIdUserA);

        tokenMap.put(vxUserId, vxTokenUserA);

        return vxUser;
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
                return scanner.useDelimiter("\\A").next();
            }
        }
    }

    @Test
    public void payCreateTest00() throws
            StripeException,
            FirebaseAuthException,
            IOException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        VxUser vxUser = setupFullUser("acct_1OO0j2PVTA3jVN7Z");
        String vxToken = tokenMap.get(vxUser.id);

        String title = "Event of " + vxUser.email;
        EventCreateParams params = EventCreateParams.builder()
                .vxUserId(vxUser.id)
                .type(VxEvent.Type.payed1V1)
                .vxIntegrationId(VxIntegrationId.vxGaming)
                .vxGame(VxGame.leagueOfLegends)
                .title(title)
                .currency("eur")
                .entryPrice(1000L)
                .build();

        EventPayCreateResponse eventPayCreateResponse = EventHelper.payCreate(restTemplate, port, vxToken, params, 200);

        String fileName = "payEventIntegrationTest/payCreateTest00.json";
        String fileContent = loadFileAsString(fileName);

        String webhookSigningSecret = "whsec_b36f59fd7556a24cbdd59589110a616aebb7a35167d04d2aade484c8a345af53";
        String body = fileContent.replace("#tagStripeSessionId", eventPayCreateResponse.stripeSessionId);
        long timeStamp = (new Date()).getTime();
        String payload = timeStamp + "." + body;
        String signedPayload = Webhook.Util.computeHmacSha256(webhookSigningSecret, payload);
        String stripeSignature = "t=" + timeStamp + ",v1=" + signedPayload;

        WebhookHelper.handleStripeWebhook(restTemplate,
                port,
                stripeSignature,
                body,
                200);

        System.out.println("Use 4000000000000077 test card");
        System.out.println("URL: "+ eventPayCreateResponse.stripeSessionPaymentUrl);

        Assertions.assertNotNull(vxUser);
    }


}
