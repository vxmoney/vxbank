package eu.vxbank.api.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.EventCloseParams;
import eu.vxbank.api.endpoints.event.dto.EventCloseResponse;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateParams;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateResponse;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultListResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.EventHelper;
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
import vxbank.datastore.data.service.VxDsService;

import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Close1V1EventIntegrationTest {
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
    public void testClose1v1() throws StripeException, FirebaseAuthException, JsonProcessingException {

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


        // creator set result
        {
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

        // participant set result
        {
            Long timeStampParticipant = new Date().getTime();
            EventResultCreateParams participantParams = EventResultCreateParams.builder()
                    .vxUserId(whoJoins.id)
                    .vxEventId(vxEvent.id)
                    .createTimeStamp(timeStampParticipant)
                    .participantId(creator.id) // confirms that creator is firstPlace
                    .participantFinalResultPlace(VxEventResult.FinalResultPlace.firstPlace)
                    .prizeValue(600L)
                    .build();

            EventResultCreateResponse participantResult = EventResultHelper.create(restTemplate,
                    port,
                    whoJoins.vxToken,
                    participantParams,
                    200);
            Assertions.assertNotNull(participantResult.vxEventId);
        }

        List<VxEventResult> resultList = VxDsService.getListByEventId(VxEventResult.class,
                systemService.getVxBankDatastore(),
                vxEvent.id);

        //

        EventCloseParams closeParams = new EventCloseParams();
        closeParams.vxEventId = vxEvent.id;
        EventCloseResponse response = EventHelper.closeEvent(restTemplate, port, creator.vxToken, closeParams, 200);
        Assertions.assertEquals(VxEvent.State.closed, response.state);

        // try to close second time but it should fail fast
        EventHelper.closeEvent(restTemplate, port, creator.vxToken, closeParams, 500);

        // list the results using the api
        EventResultListResponse resultListResponse = EventResultHelper.getByEventId(restTemplate,
                port,
                creator.vxToken,
                vxEvent.id,
                200);
        Assertions.assertEquals(2, resultListResponse.eventResultList.size());

    }

}
