package eu.vxbank.api.endpoints.publicevent.orderitem;

import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.publicevent.VxPublicEventOrderItem;

@RestController
@RequestMapping("/publicEventOrderItem")
public class PublicEventOrderItemEndpoint {
    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @GetMapping("/{itemId}")
    @ResponseBody
    public VxPublicEventOrderItem get(Authentication auth, @PathVariable Long itemId) {
        throw new IllegalStateException("Please implement this");
    }
}
