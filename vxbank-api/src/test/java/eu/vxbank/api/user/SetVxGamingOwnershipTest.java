package eu.vxbank.api.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import eu.vxbank.api.ApplicationProps;
import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.security.PublicKey;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SetVxGamingOwnershipTest {

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

    @Test
    public void test00OwnershipVxGaming() throws FirebaseAuthException, JsonProcessingException, StripeException {
        System.out.println("Hello test test00OwnershipVxGaming");

        PingResponse pingResponse = PingHelper.getEnvironment(restTemplate, port, 200);
        ApplicationProps applicationProps = pingResponse.applicationProps;
        System.out.println("Ping response environment: " + pingResponse.environment);
    }
}
