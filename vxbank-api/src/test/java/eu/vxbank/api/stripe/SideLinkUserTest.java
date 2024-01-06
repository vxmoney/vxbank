package eu.vxbank.api.stripe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import eu.vxbank.api.endpoints.ping.dto.PingRequestFundsParams;
import eu.vxbank.api.endpoints.ping.dto.PingRequestFundsResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.helpers.StripeConfigHelper;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.utils.components.SystemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SideLinkUserTest {

    private static final String testPassword = "secured-test-password";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${stripeKey.devSecretKey}")
    private String stripeSecretKey;

    @Autowired
    SystemService systemService;

    @Test
    public void sideLinkUserAndAddFundsTest() throws FirebaseAuthException, JsonProcessingException {

        boolean testDisabled = false;
        if (testDisabled) {
            System.out.println("End of test");
            return;
        }

        String vxToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4IiwidWlkIjo4LCJpc3MiOiJzZWxmIiwiZXhwIjoxNzA0NTU4OTk2LCJpYXQiOjE3MDQ1NTE3OTYsImVtYWlsIjoidXNlcl8xNzA0NTUxNzk1NjQ4QGV4YW1wbGUuY29tIn0.WkPQ1gHFsoOdfCVp7Ldr1tk4hUopdaryRoiQ5U6hIGa6FiXAXr0RwqJy0mKEhgJjmuu7m1LqJbCE83s5H6JuTTN1uE98bwOZMD9y1iFXrc0rD2TF36jQt3kptWZusxDZYeecSzuzEyiT1gHfkCgsX-utJSTrtnWBavMcHwptOo5HsZ--HcZ5sSmukgow_3eEzZcYhDC9e49odIGRYGR6KaiYSGnt9CBsKIyvPcWn6G54KsVkFlrywnU-oeUG2cVR_2_2ikMp9TMwx1FbA_WK_un12vFo0PzorHn6vpowTY7s9vRTqO8J2KDzDrffXIAqM8likbnNRTOTv_4cJ7n6lA";
        String stripeId = "acct_1OUyNpB1kQZowFak";

        LoginResponse loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = loginResponse.id;
        StripeConfigHelper.initiateConfig(vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        SideStripeConfigHelper.setStripeAccountId(systemService.getVxBankDatastore(), loginResponse.id, stripeId);

        PingRequestFundsParams params = PingRequestFundsParams.builder()
                .userId(loginResponse.id)
                .amount(1000L)
                .currency("eur")
                .build();
        PingRequestFundsResponse requestFundsResponse = PingHelper.requestFunds(restTemplate,
                port,
                vxToken,
                params,
                200);

    }
}
