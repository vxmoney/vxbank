package eu.vxbank.api.endpoints.eventparticipant;

import eu.vxbank.api.endpoints.eventparticipant.dto.EventParticipantGetByEventIdResponse;
import eu.vxbank.api.utils.components.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEventParticipant;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;

@RestController
@RequestMapping("/eventparticipant")
public class EventParticipantEndpoint {

    @Autowired
    SystemService systemService;

    @GetMapping("/getByEventId/{eventId}")
    @ResponseBody
    public EventParticipantGetByEventIdResponse getByEventId(@PathVariable Long eventId) {

        VxBankDatastore ds = systemService.getVxBankDatastore();

        //VxDsService.get(params.vxPaymentId, VxPayment.class, ds);
        List<VxEventParticipant> participantList = VxDsService.getListByEventId(ds, eventId, VxEventParticipant.class);

        EventParticipantGetByEventIdResponse response = new EventParticipantGetByEventIdResponse();
        response.participantList = participantList;
        return response;
    }
}
