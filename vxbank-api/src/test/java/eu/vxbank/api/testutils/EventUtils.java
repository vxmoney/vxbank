package eu.vxbank.api.testutils;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.event.dto.EventJoinParams;
import eu.vxbank.api.endpoints.event.dto.EventJoinResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.EventHelper;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.web.client.TestRestTemplate;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEvent;

public class EventUtils {
    public static VxEvent create1v1Event(TestRestTemplate restTemplate,
                                         int port,
                                         String stripeSecretKey,
                                         VxBankDatastore vxBankDatastore,
                                         LoginResponse creator,
                                         LoginResponse whoJoins,
                                         Long price,
                                         String currency) throws StripeException {

        // send funds
        VxStripeUtil.sendFundsToStripeAccount(stripeSecretKey, creator.stripeId, price, currency);
        VxStripeUtil.sendFundsToStripeAccount(stripeSecretKey, whoJoins.stripeId, price, currency);

        // create event
        String title = "Event " + creator.id;
        EventCreateParams params = EventCreateParams.builder()
                .vxUserId(creator.id)
                .type(VxEvent.Type.payed1V1)
                .vxIntegrationId(VxIntegrationId.vxGaming)
                .title(title)
                .currency(currency)
                .entryPrice(price)
                .build();

        EventCreateResponse eventCreateResponse = EventHelper.create(restTemplate, port, creator.vxToken, params, 200);

        ModelMapper mm = new ModelMapper();
        VxEvent vxEvent = mm.map(eventCreateResponse, VxEvent.class);

        // join event
        EventJoinParams joinParams = EventJoinParams.builder()
                .eventId(vxEvent.id)
                .vxUserId(whoJoins.id)
                .build();

        EventHelper.join(restTemplate, port, whoJoins.vxToken, joinParams, 200);


        return vxEvent;
    }
}
