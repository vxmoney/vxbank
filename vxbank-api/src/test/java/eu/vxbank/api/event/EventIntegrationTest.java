package eu.vxbank.api.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.*;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.utils.components.SystemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventIntegrationTest {
    private static final String testPassword = "secured-test-password";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    String stripeAccountIdUserA = "acct_1OO0j2PVTA3jVN7Z";
    private VxUser userA;
    private String vxTokenUserA;

    private void setupFullUserA() throws FirebaseAuthException, JsonProcessingException, StripeException {

        // stripe id: acct_1OO0j2PVTA3jVN7Z


        String email = RandomUtil.generateRandomEmail();
        String vxToken = UserHelper.generateVxToken(email, restTemplate, port);
        vxTokenUserA = vxToken;




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
        userA = vxUser;
    }

    @BeforeEach
    public void setup() throws FirebaseAuthException, JsonProcessingException, StripeException {

       setupFullUserA();

        System.out.println("Hello setup");
    }

    @Test
    public void test00Create() {


        Assertions.assertNotNull(userA);
        EventCreateParams params = new EventCreateParams();
        LoginResponse pingResponse = PingHelper.whoAmI(vxTokenUserA, restTemplate, port,200);
        int expectedStatusCode = 200;
        EventCreateResponse eventCreateResponse = EventHelper.create(

                restTemplate, port,vxTokenUserA, params, expectedStatusCode);

        System.out.println("End of test");
    }


}
