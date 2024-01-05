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

        String vxToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4IiwidWlkIjo4LCJpc3MiOiJzZWxmIiwiZXhwIjoxNzA0NDU1NTIwLCJpYXQiOjE3MDQ0NDgzMjAsImVtYWlsIjoidXNlcl8xNzA0NDQ4MzIwODExQGV4YW1wbGUuY29tIn0.XuNdYq0T0o0MJ9FBX78K2bfvU-wkyHwB3oRnI1Y1-JvhzNQTIRRmGc1dsxz9ZM3gIoBNp2uS12mghIpfmMz5reo3WQ1eAuRMmT6JjNLy0hOo5VmrvhsMSZFcncYNzx_1_nWAvhCZdoWvC7fbDnVgmHUWfGrmYlt-tNGC2Hz4elJ4LQih_QSQmHxw72V-HY51UU3OafAvHhGm1D3Lr4EY40hhjkA0dQ4MKyreG0A5zE-O4Q2WUfd8yBYS8XmEvB6Vy-ttbm7PS4bnj2Xo7jWU8loluVQxQ-gc_nop4kVa6vg49IdjZQE1He4lIaUAONetL7Zkz3z6xY54JFTzX12qaA";
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
