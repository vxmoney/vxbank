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

        String vxToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIiwidWlkIjoxLCJpc3MiOiJzZWxmIiwiZXhwIjoxNzA0NDUxNTA4LCJpYXQiOjE3MDQ0NDQzMDgsImVtYWlsIjoidXNlcl8xNzA0NDQ0MzA3Nzg1QGV4YW1wbGUuY29tIn0.CJSy_oMtMFzmpajEs4Sy07vDiKggttczavYp3dut8DMd1P0Pyvc-zB7BmxJ1Q425Y8Zy5TwGkjfFKr0IpoFCjHm1HiaKxX7E3hY7ZhqFc1l7tbqe1LKJLef7tEh4EAf2uKPslxNjb6c0wTSz_NVlRqCSACMMII9a4kdFENRGCVFOWvQvRF4VgwTAW6nQWT_h48NWco9a-14imYZQ9k12BKLQ0zsV4buRNIr4RjwLEqlPwFBPn4B320mdnNMDafd1LZVOUWkQH2lDghVv9IEuqCNe5Z0E2Lv1oNnKz8-LrlVopLmzBZzcSagaRBJzgG3SmacDOnU8Iwjxy7gsKc0yIg";
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
