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

        boolean testDisabled = true;
        if (testDisabled) {
            System.out.println("End of test");
            return;
        }

        String vxToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIyMiIsInVpZCI6MjIsImlzcyI6InNlbGYiLCJleHAiOjE3MDQ0MTY3NTEsImlhdCI6MTcwNDQwOTU1MSwiZW1haWwiOiJ1c2VyXzE3MDQ0MDk1NTE2ODVAZXhhbXBsZS5jb20ifQ.VvucKrY-N154Uem3wsVwNYMSeYRQ62_EsJdTRnK0M43yTEmNxPAhFVd1UBQErfO0LW1vl-VIhYlgUjHT1ECYlTVgyCqWjL1YOEpo6WgRp0bzFLUY9Sce7_DGcnxF2CzhnQU7ZuvYHTyqqauU10zG-2QCYwBkhA8vQgGMxhFYMH3zsGv_VLsaZeMBpFHY7Z4IbTMh9YhmKc7fFoLrOf423yW3WfIQOkOADi7R6TwDJj3cLhvt6cCoazgh-DJ2wRDShH1tLx7F3NUcJk2JfpsCatw6nWxMrsHLO-4hZn4mub7cZEOWG-PwA9gvR2ey302ygMtcysqBAPY_CPxa3Es94A";
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
