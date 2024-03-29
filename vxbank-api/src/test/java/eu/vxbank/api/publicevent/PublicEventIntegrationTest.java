package eu.vxbank.api.publicevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateResponse;
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
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxGame;
import vxbank.datastore.data.models.VxUser;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicEventIntegrationTest {

    private class Setup{
        Long userId;
        String vxToken;
        String stripeAccountId;
        String email;

    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    private Setup setupFullUser(  String stripeAccountId) throws FirebaseAuthException, JsonProcessingException, StripeException {

        // stripe id: acct_1OO0j2PVTA3jVN7Z

        Setup setup = new Setup();

        setup.email = RandomUtil.generateRandomEmail();
        setup.vxToken = UserHelper.generateVxToken(setup.email, restTemplate, port);



        LoginResponse loginResponse = PingHelper.whoAmI(setup.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(setup.email, loginResponse.email);


        setup.userId = loginResponse.id;

        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = setup.userId;
        StripeConfigInitiateConfigResponse initiateConfigResponse = StripeConfigHelper.initiateConfig(setup.vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);



        VxBankDatastore ds = systemService.getVxBankDatastore();
        SideStripeConfigHelper.setStripeAccountId(ds, setup.userId, setup.stripeAccountId);

        return setup;
    }

    @Test
    public void createTest000() throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup setup = setupFullUser("acct_1OO0j2PVTA3jVN7Z");
        Long timeStamp = new Date().getTime();
        String title = "Event - "+timeStamp;
        PublicEventCreateParams params = PublicEventCreateParams.builder()
                .vxUserId(setup.userId)
                .vxIntegrationId(VxIntegrationId.vxEvents)
                .title(title)
                .currency("eur")
                .build();

        PublicEventCreateResponse publicEventCreateResponse
                = PublicEventHelper.create(restTemplate,
                port,
                setup.vxToken,
                params,
                200);
    }
}
