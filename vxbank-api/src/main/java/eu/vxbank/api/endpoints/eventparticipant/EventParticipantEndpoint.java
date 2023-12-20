package eu.vxbank.api.endpoints.eventparticipant;

import eu.vxbank.api.endpoints.event.dto.EventGetResponse;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxEventPayment;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;

@RestController
@RequestMapping("/eventparticipant")
public class EventParticipantEndpoint {

    @GetMapping("/getByEventId{eventId}")
    @ResponseBody
    public EventGetResponse getByEventId(@PathVariable Long eventId) {

       throw new IllegalStateException("Please implement this getByEventId");

    }
}
