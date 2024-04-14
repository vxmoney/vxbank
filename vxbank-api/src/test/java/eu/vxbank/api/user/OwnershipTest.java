package eu.vxbank.api.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.payment.PaymentTest;
import eu.vxbank.api.utils.components.vxintegration.VxIntegration;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Use this test to add funds if test fails
 * {@link PaymentTest#createPaymentTest()}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OwnershipTest {

    private static final String vxGamingStripeId = "acct_1ONYExBIJYpsQyBC";
    private static final Long vxGamingVxBanId = 1L;
    private static final String vxGamingEmail = "admin@vxgaming.com";


    private static final String testPassword = "not-important-SetVxGamingOwnershipTest";

    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @Test
    public void test00ConfigTest() throws FirebaseAuthException, JsonProcessingException, StripeException {
        System.out.println("Hello test test00OwnershipVxGaming");

        PingResponse pingResponse = PingHelper.getEnvironment(restTemplate, port, 200);
        VxIntegrationConfig responseIntegrationConfig = pingResponse.vxIntegrationConfig;
        Assertions.assertNotNull(responseIntegrationConfig);
        Assertions.assertEquals(3, responseIntegrationConfig.vxIntegrationList.size());

        Assertions.assertEquals(vxIntegrationConfig, responseIntegrationConfig);

        System.out.println("Ping response environment: " + pingResponse.environment);
    }

    @Test
    public void test00SendFundsToVxBank() throws FirebaseAuthException, JsonProcessingException, StripeException {
        VxIntegration bankIntegration = vxIntegrationConfig.getIntegrationById(VxIntegrationId.vxBank);

        Long amount = 1000L; // in web2 we use 2 decimals (this = 10)
        Transfer transfer = VxStripeUtil.sendFundsToStripeAccount(stripeDevSecretKey,
                bankIntegration.vxStripeId,
                amount,
                "eur");

        String message = String.format(
                "View VxBank in stripe: https://dashboard.stripe" +
                        ".com/test/connect/accounts/%s/activity", bankIntegration.vxStripeId);
        System.out.println(message);

    }

    @Test
    public void test01SendFundsToVxGamin() throws FirebaseAuthException, JsonProcessingException, StripeException {
        VxIntegration gaminIntegration = vxIntegrationConfig.getIntegrationById(VxIntegrationId.vxGaming);

        Long amount = 1000L; // in web2 we use 2 decimals (this = 10)
        Transfer transfer = VxStripeUtil.sendFundsToStripeAccount(stripeDevSecretKey,
                gaminIntegration.vxStripeId,
                amount,
                "eur");
        String message = String.format(
                "View VxGamin in stripe: https://dashboard.stripe" +
                        ".com/test/connect/accounts/%s/activity", gaminIntegration.vxStripeId);
        System.out.println(message);
    }


}
