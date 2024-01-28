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

    /**
     * How to run this test.
     * Start local server
     * - bootRun
     * Configure frontend and connect to local host
     * - openVsCode
     * - . scripts/configDevEnviromentScripts.sh
     * - initEnvLocalhost
     * - npn install
     * - npn run dev
     * - /DeveloperExamples/Localhost auth/ Generate user and login
     * Use the vxtoken generated and paste in this text.
     */
    @Test
    public void sideLinkUserAndAddFundsTest() throws FirebaseAuthException, JsonProcessingException {

        boolean testDisabled = false;
        if (testDisabled) {
            System.out.println("End of test");
            return;
        }

        String vxToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxMjQiLCJ1aWQiOjEyNCwiaXNzIjoic2VsZiIsImV4cCI6MTcwNjQ0ODQ1MCwiaWF0IjoxNzA2NDQxMjUwLCJlbWFpbCI6InVzZXJfMTcwNjQ0MTI1MDY1NEBleGFtcGxlLmNvbSJ9.Fb7pRkIh6pYiJfhVINYTdpb0Nu4eA_AzIFaSmVf54-ymQMTRl6dxU0bzn9ghfwMrZ3uyBbIcwEXg96QPMN0Vg2CcY_ENVu7IJKNvNcCpRx6imEV9XGMvBl2KsubPzD8XFMzYp-69TCStyJ8i97baXmE9lwMyYBKu_Cm42Sn1fGazowqtqBq_mN0khmW4oIS_ohXklICBg6NA5KyzXboEHbjgBJubzNKRjW5r1VzH1Yq4PcyVI1sZHQJSc9nocQcTN5WR-YHn_Z1aQoPiibbu8mJI3TY7iedTeCo0SVrBrFm770a1crGpSLxcrghxCPwSgaFmpHs6He4kRaPp2IVXgA";
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
