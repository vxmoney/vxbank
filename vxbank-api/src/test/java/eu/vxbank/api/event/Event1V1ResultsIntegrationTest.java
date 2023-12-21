package eu.vxbank.api.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.EventJoinParams;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateParams;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.EventResultHelper;
import eu.vxbank.api.sidehelpers.SideCompleteUser;
import eu.vxbank.api.testutils.EventUtils;
import eu.vxbank.api.utils.components.SystemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxEventResult;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Event1V1ResultsIntegrationTest {
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
    public void testResultsBasicFlow() throws StripeException, FirebaseAuthException, JsonProcessingException {

        LoginResponse creator = SideCompleteUser.setupUser("acct_1OPVGOPkbRPH6pWE",
                restTemplate,
                port,
                systemService.getVxBankDatastore());

        LoginResponse whoJoins = SideCompleteUser.setupUser("acct_1OPVKOBU1TJus18q",
                restTemplate,
                port,
                systemService.getVxBankDatastore());

        Long price = 1000L;
        String curency = "eur";
        VxEvent vxEvent = EventUtils.create1v1Event(restTemplate,
                port,
                stripeSecretKey,
                systemService.getVxBankDatastore(),
                creator,
                whoJoins,
                price,
                curency);


        // set result
        Long timeStampCreator = new Date().getTime();
        EventResultCreateParams creatorParams = EventResultCreateParams.builder()
                .vxUserId(creator.id)
                .vxEventId(vxEvent.id)
                .createTimeStamp(timeStampCreator)
                .participantId(creator.id)
                .participantFinalResultPlace(VxEventResult.FinalResultPlace.firstPlace)
                .prizeValue(600L)
                .build();

        EventResultCreateResponse resultResponse = EventResultHelper.create(restTemplate,
                port,
                creator.vxToken,
                creatorParams,
                200);
        Assertions.assertNotNull(resultResponse.vxEventId);



    }

}
