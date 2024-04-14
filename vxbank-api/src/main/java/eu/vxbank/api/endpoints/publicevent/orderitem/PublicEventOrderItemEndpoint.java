package eu.vxbank.api.endpoints.publicevent.orderitem;

import eu.vxbank.api.endpoints.publicevent.orderitem.dto.OrderItemSearchResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.publicevent.VxPublicEventOrderItem;
import vxbank.datastore.data.service.VxDsService;

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
        systemService.validateAndGetUser(auth);
        VxPublicEventOrderItem item = VxDsService.getById(VxPublicEventOrderItem.class, systemService.getVxBankDatastore(), itemId);
        return item;
    }

    @GetMapping("/get/byLongIndexField")
    @ResponseBody
    public OrderItemSearchResponse getByLongIndexField(Authentication auth,
                                                       @RequestParam VxPublicEventOrderItem.IndexedField indexedField,
                                                       @RequestParam Long value) {
        throw new UnsupportedOperationException("Please implement this");
    }
}
