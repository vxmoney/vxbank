package eu.vxbank.api.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.*;
import eu.vxbank.api.endpoints.eventparticipant.dto.EventParticipantGetByEventIdResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.*;
import eu.vxbank.api.sidehelpers.SideCompleteUser;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxUser;

import java.util.Arrays;
import java.util.List;

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
    public void test00Create() throws StripeException {

        Long eventPrice = 1000L;


        Assertions.assertNotNull(userA);
        String title = "Event of " + userA.email;
        EventCreateParams params = EventCreateParams.builder()
                .vxUserId(userA.id)
                .type(VxEvent.Type.payed1V1)
                .vxIntegrationId(VxIntegrationId.vxGaming)
                .title(title)
                .currency("eur")
                .entryPrice(1000L)
                .build();

        // try to create when client is broke
        EventHelper.create(restTemplate, port, vxTokenUserA, params, 500);

        // add some funds and try to create again
        VxStripeUtil.sendFundsToStripeAccount(stripeDevSecretKey, stripeAccountIdUserA, eventPrice, "eur");

        EventCreateResponse eventCreateResponse = EventHelper.create(restTemplate, port, vxTokenUserA, params, 200);

        Assertions.assertEquals(userA.id, eventCreateResponse.vxUserId);
        Assertions.assertEquals(title, eventCreateResponse.title);
        Assertions.assertEquals(VxIntegrationId.vxGaming, eventCreateResponse.vxIntegrationId);

        EventGetResponse getResponse = EventHelper.get(restTemplate, port, vxTokenUserA, eventCreateResponse.id, 200);
        Assertions.assertEquals(eventCreateResponse.id, getResponse.id);
        Assertions.assertEquals(eventPrice, getResponse.availableFunds);

        EventParticipantGetByEventIdResponse participantResponse = EventParticipantHelper.getByEventId(restTemplate,
                port,
                vxTokenUserA,
                eventCreateResponse.id,
                200);
        Assertions.assertEquals(1, participantResponse.participantList.size());
    }

    @Test
    public void testSearch() throws StripeException {

        Long eventPrice = 1000L;


        Assertions.assertNotNull(userA);
        String title = "Event of " + userA.email;
        EventCreateParams params = EventCreateParams.builder()
                .vxUserId(userA.id)
                .type(VxEvent.Type.payed1V1)
                .vxIntegrationId(VxIntegrationId.vxGaming)
                .title(title)
                .currency("eur")
                .entryPrice(1000L)
                .build();

        // add some funds and try to create again
        VxStripeUtil.sendFundsToStripeAccount(stripeDevSecretKey, stripeAccountIdUserA, eventPrice, "eur");

        EventCreateResponse eventCreateResponse = EventHelper.create(restTemplate, port, vxTokenUserA, params, 200);

        int offset = 0;
        int limit = 10000;
        List<VxEvent.State> stateList = Arrays.asList(VxEvent.State.inProgress, VxEvent.State.openForRegistration);

        EventSearchResponse eventSearchResponse = EventHelper.search(restTemplate,
                port,
                vxTokenUserA,
                VxIntegrationId.vxGaming,
                stateList,
                offset,
                limit,
                200);
        Assertions.assertNotNull(eventSearchResponse);
        System.out.println("Total events in emulator: " + eventSearchResponse.eventList.size());
    }

    //acct_1OPVGOPkbRPH6pWE UserA
    //acct_1OPVKOBU1TJus18q UserB

    @Test
    public void testJoin() throws StripeException, FirebaseAuthException, JsonProcessingException {

        String stripeIdA = "acct_1OPVGOPkbRPH6pWE";
        LoginResponse vxLoginA = SideCompleteUser.setupUser(stripeIdA,
                restTemplate,
                port,
                systemService.getVxBankDatastore());

        String stripeIdB = "acct_1OPVKOBU1TJus18q";
        LoginResponse vxLoginB = SideCompleteUser.setupUser(stripeIdB,
                restTemplate,
                port,
                systemService.getVxBankDatastore());


        EventCreateResponse createEventResponse = create1V1Event(restTemplate,
                port,
                systemService.getVxBankDatastore(),
                stripeIdA,
                vxLoginA,
                1000L,
                "eur");

        // join the event
        EventJoinParams params = EventJoinParams.builder()
                .eventId(createEventResponse.id)
                .vxUserId(vxLoginB.id)
                .build();

        EventHelper.join(restTemplate, port, vxLoginB.vxToken, params, 500);
        VxStripeUtil.sendFundsToStripeAccount(stripeDevSecretKey,
                stripeIdB,
                createEventResponse.entryPrice,
                createEventResponse.currency);

        EventJoinResponse joinResponse = EventHelper.join(restTemplate, port, vxLoginB.vxToken, params, 200);
        Assertions.assertNotNull(joinResponse);
        Assertions.assertEquals(2, joinResponse.eventActiveParticipantsCount);


    }

    private EventCreateResponse create1V1Event(TestRestTemplate restTemplate,
                                               int port,
                                               VxBankDatastore vxBankDatastore,
                                               String stripeIdA,
                                               LoginResponse loginResponse,
                                               Long eventPrice,
                                               String currency) throws StripeException {
        // send funds
        VxStripeUtil.sendFundsToStripeAccount(stripeDevSecretKey, stripeIdA, eventPrice, currency);

        String title = "Event " + loginResponse.id;
        EventCreateParams params = EventCreateParams.builder()
                .vxUserId(loginResponse.id)
                .type(VxEvent.Type.payed1V1)
                .vxIntegrationId(VxIntegrationId.vxGaming)
                .title(title)
                .currency(currency)
                .entryPrice(eventPrice)
                .build();

        EventCreateResponse eventCreateResponse = EventHelper.create(restTemplate,
                port,
                loginResponse.vxToken,
                params,
                200);

        return eventCreateResponse;


    }


}
