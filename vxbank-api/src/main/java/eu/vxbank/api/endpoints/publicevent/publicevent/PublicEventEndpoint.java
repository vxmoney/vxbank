package eu.vxbank.api.endpoints.publicevent.publicevent;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEvent;
import vxbank.datastore.data.service.VxDsService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/publicEvent")
public class PublicEventEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @PostMapping
    public PublicEventCreateResponse create(Authentication auth, @RequestBody PublicEventCreateParams params) throws
            StripeException {

        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        if (!Objects.equals(vxUser.id, params.vxUserId)) {
            throw new IllegalStateException("You can not create events for someone else");
        }

        VxPublicEvent publicEvent = VxPublicEvent.builder()
                .vxUserId(vxUser.id)
                .vxIntegrationId(params.vxIntegrationId.toString())
                .managerIdList(Collections.singletonList(vxUser.id))
                .title(params.title)
                .currency(params.currency)
                .build();
        VxDsService.persist(VxPublicEvent.class, systemService.getVxBankDatastore(), publicEvent);

        ModelMapper mm = new ModelMapper();
        PublicEventCreateResponse response = mm.map(publicEvent, PublicEventCreateResponse.class);
        return response;
    }

    @GetMapping("/{eventId}")
    @ResponseBody
    public PublicEventCreateResponse get(Authentication auth, @PathVariable Long eventId) {
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        VxPublicEvent publicEvent = VxDsService.getById(VxPublicEvent.class,
                systemService.getVxBankDatastore(),
                eventId);
        List<Long> managerIdList = publicEvent.managerIdList;
        if (managerIdList == null || !managerIdList.contains(vxUser.id)) {
            throw new IllegalStateException("You are not allowed to read full VxPublicEvent response");
        }

        ModelMapper mm = new ModelMapper();
        PublicEventCreateResponse response = mm.map(publicEvent, PublicEventCreateResponse.class);
        return response;
    }

}
