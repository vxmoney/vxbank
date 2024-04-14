package eu.vxbank.api.endpoints.publicevent.sellingpoint;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointCreateParams;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointCreateResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEvent;

import static eu.vxbank.api.endpoints.publicevent.tools.PublicEventEndpointTools.checkUserIsOwnerOfEvent;
import static eu.vxbank.api.endpoints.publicevent.tools.PublicEventEndpointTools.getVxEvent;

@RestController
@RequestMapping("/publicEventSellingPoint")
public class PublicEventSellingPointEndpoint {
    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @PostMapping
    public SellingPointCreateResponse create(Authentication auth, @RequestBody SellingPointCreateParams params) throws
            StripeException {

        VxUser currentUser = systemService.validateAndGetUser(auth);
        VxPublicEvent vxPublicEvent = getVxEvent(systemService.getVxBankDatastore(), params.vxPublicEventId);
        checkUserIsOwnerOfEvent(currentUser, vxPublicEvent);



        throw new IllegalStateException("Please implement this");

    }
}
