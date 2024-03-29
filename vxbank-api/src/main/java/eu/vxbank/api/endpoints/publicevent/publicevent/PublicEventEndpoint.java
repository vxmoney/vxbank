package eu.vxbank.api.endpoints.publicevent.publicevent;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventSearchResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEvent;
import vxbank.datastore.data.publicevent.VxPublicEventManager;
import vxbank.datastore.data.service.VxDsService;

import java.util.*;
import java.util.stream.Collectors;

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
                .title(params.title)
                .currency(params.currency)
                .build();
        VxDsService.persist(VxPublicEvent.class, systemService.getVxBankDatastore(), publicEvent);

        VxPublicEventManager publicEventManager = VxPublicEventManager.builder()
                .userId(vxUser.id)
                .publicEventId(publicEvent.id)
                .build();
        VxDsService.persist(VxPublicEventManager.class, systemService.getVxBankDatastore(), publicEventManager);

        ModelMapper mm = new ModelMapper();
        PublicEventCreateResponse response = mm.map(publicEvent, PublicEventCreateResponse.class);
        response.managerIdList = Collections.singletonList(vxUser.id);
        return response;
    }

    @GetMapping("/{eventId}")
    @ResponseBody
    public PublicEventCreateResponse get(Authentication auth, @PathVariable Long eventId) {
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);


        if (!userIsPublicEventManger(vxUser.id, eventId)) {
            throw new IllegalStateException("You are not VxPublicEvent manager");
        }

        // get event
        VxPublicEvent publicEvent = VxDsService.getById(VxPublicEvent.class,
                systemService.getVxBankDatastore(),
                eventId);
        ModelMapper mm = new ModelMapper();
        PublicEventCreateResponse response = mm.map(publicEvent, PublicEventCreateResponse.class);

        // get managers
        List<VxPublicEventManager> managerList = VxDsService.getByPublicEventId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(),
                eventId);
        List<Long> managerIdList = managerList.stream()
                .map(VxPublicEventManager::getUserId)
                .toList();
        response.managerIdList = managerIdList;

        return response;
    }

    private boolean userIsPublicEventManger(Long id, Long eventId) {
        List<VxPublicEventManager> eventList = VxDsService.getByUserId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(),
                id);
        Optional<VxPublicEventManager> optionalVxPublicEventManager = eventList.stream()
                .filter(pe -> pe.publicEventId.equals(eventId))
                .findFirst();
        return optionalVxPublicEventManager.isPresent();
    }

    @GetMapping
    @ResponseBody
    public PublicEventSearchResponse search(Authentication auth, @RequestParam(name = "vxUserId") Long vxUserId) {

        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        if (!Objects.equals(vxUser.id, vxUserId)) {
            throw new IllegalStateException("You can not search events for someone else");
        }

        List<VxPublicEvent> vxPublicEventList = VxDsService.searchPublicEvent(systemService.getVxBankDatastore(),
                vxUserId);
        PublicEventSearchResponse response = new PublicEventSearchResponse();
        response.eventList = vxPublicEventList;
        return response;
    }

}
