package eu.vxbank.api.endpoints.event;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping("/event")
public class EventEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @PostMapping
    public EventCreateResponse create(Authentication auth, @RequestBody EventCreateParams params) throws
            StripeException {
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        if (!Objects.equals(vxUser.id, params.vxUserId)) {
            throw new IllegalStateException("You can not create events for someone else");
        }

        VxStripeConfig vxStripeConfig = VxDsService.getByUserId(vxUser.id,
                        new HashMap<>(),
                        systemService.getVxBankDatastore(),
                        VxStripeConfig.class)
                .get(0);


        VxStripeUtil.chargeConnectedAccount(stripeKeys.stripeSecretKey,
                vxStripeConfig.stripeAccountId,
                params.entryPrice,
                params.currency);

        Long createTimeStamp = new Date().getTime();
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUser.id)
                .state(VxEvent.State.openForRegistration)
                .type(params.type)
                .vxIntegrationId(params.vxIntegrationId.toString())
                .title(params.title)
                .currency(params.currency)
                .entryPrice(params.entryPrice)
                .createTimeStamp(createTimeStamp)
                .build();

        VxDsService.persist(vxEvent, systemService.getVxBankDatastore(), VxEvent.class);

        ModelMapper mm = new ModelMapper();
        EventCreateResponse response = mm.map(vxEvent, EventCreateResponse.class);

        return response;
    }
}
