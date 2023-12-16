package eu.vxbank.api.endpoints.event;

import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.utils.components.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.data.models.VxUser;

@RestController
@RequestMapping("/event")
public class EventEndpoint {

    @Autowired
    SystemService systemService;

    @PostMapping
    public EventCreateResponse create (Authentication auth, @RequestBody EventCreateParams params){
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);
        throw new IllegalStateException("Please implement this");
    }
}
