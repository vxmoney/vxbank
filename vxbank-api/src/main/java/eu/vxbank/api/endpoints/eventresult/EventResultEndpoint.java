package eu.vxbank.api.endpoints.eventresult;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateParams;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultCreateResponse;
import eu.vxbank.api.endpoints.eventresult.dto.EventResultListResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.*;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/eventresult")
public class EventResultEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @PostMapping
    public EventResultCreateResponse create(Authentication auth, @RequestBody EventResultCreateParams params) throws
            StripeException {

        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        VxEvent vxEvent = VxDsService.getById(VxEvent.class, systemService.getVxBankDatastore(), params.vxEventId);

        // check current participants
        List<VxEventParticipant> participantList = VxDsService.getListByEventId(VxEventParticipant.class,
                systemService.getVxBankDatastore(),
                params.vxEventId);
        if (!listContainsUserId(participantList, params.participantId)) {
            throw new IllegalStateException(
                    "You are trying to set results for someone that did not participated in event");
        }

        VxEventResult vxEventResult = params.buildVxEventResult();
        vxEventResult.state = VxEventResult.State.active;
        VxDsService.persist(VxEventResult.class, systemService.getVxBankDatastore(), vxEventResult);
        EventResultCreateResponse response = EventResultCreateResponse.newInstance(vxEventResult);

        return response;
    }

    private boolean listContainsUserId(List<VxEventParticipant> list, Long vxUserId) {
        Optional<VxEventParticipant> optionalParticipant = list.stream()
                .filter(p -> p.vxUserId.equals(vxUserId))
                .findFirst();
        return optionalParticipant.isPresent();
    }

    @GetMapping("/getByEventId/{eventId}")
    @ResponseBody
    public EventResultListResponse getByEventId(@PathVariable Long eventId) {
        List<VxEventResult> resultList = VxDsService.getListByEventId(VxEventResult.class,
                systemService.getVxBankDatastore(),
                eventId);
        EventResultListResponse response = EventResultListResponse.newInstance(resultList);
        return response;
    }

}
