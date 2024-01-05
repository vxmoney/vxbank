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

        String vxToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIiwidWlkIjoxLCJpc3MiOiJzZWxmIiwiZXhwIjoxNzA0NDk0NTA0LCJpYXQiOjE3MDQ0ODczMDQsImVtYWlsIjoidXNlcl8xNzA0NDg3MzAzOTI1QGV4YW1wbGUuY29tIn0.LpAUgpMAphhf2i_Tb21bmBmOFCQ09q4aQPbupj0eVKctrxfTm0NapyyVt-qG4hjkPaWGGdj5Gc29f7jS8Rso-Q-tdpeu1A3A2cPNAMkjhskWTBbODgp4opEe6Tf4HyJbPuwQPslroVw1swKVKem4fV-8siTlOpPukTmD1O6FIXO010ZEU4cZ5V-YkfeEf87gvYj-ccjWB3AA04bOeMHmZRStmP4ARaNu8AZm1o-MXfolDYYpCmHRKMwhrAN0rKxtW4OKIBgdBhtzr9V8CHYHYKGEtey5EHReVyhHwEcoURq_EMnQHi2z2j0u7MxD_Ht_yzvIiFzDK5bbIh196euPwg";
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
