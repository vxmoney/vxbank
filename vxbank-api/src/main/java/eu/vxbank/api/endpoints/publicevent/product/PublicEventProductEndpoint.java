package eu.vxbank.api.endpoints.publicevent.product;


import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.product.dto.ProductCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCreateResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEvent;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;
import vxbank.datastore.data.service.VxDsService;

import static eu.vxbank.api.endpoints.publicevent.tools.PublicEventEndpointTools.checkUserIsOwnerOfEvent;
import static eu.vxbank.api.endpoints.publicevent.tools.PublicEventEndpointTools.getVxEvent;

@RestController
@RequestMapping("/publicEventProduct")
public class PublicEventProductEndpoint {


    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @PostMapping
    public VxPublicEventProduct create(Authentication auth, @RequestBody ProductCreateParams params) throws
            StripeException {

        VxUser currentUser = systemService.validateAndGetUser(auth);
        VxPublicEvent vxPublicEvent = getVxEvent(systemService.getVxBankDatastore(), params.vxPublicEventId);
        checkUserIsOwnerOfEvent(currentUser, vxPublicEvent);

        Long timestamp = System.currentTimeMillis();
        VxPublicEventProduct vxPublicEventProduct = VxPublicEventProduct.builder()
                .vxPublicEventId(params.vxPublicEventId)
                .title(params.title)
                .description(params.description)
                .availability(params.availability)
                .price(params.price)
                .createTimeStamp(timestamp)
                .updateTimeStamp(timestamp)
                .build();

        VxDsService.persist(VxPublicEventProduct.class, systemService.getVxBankDatastore(), vxPublicEventProduct);


        return vxPublicEventProduct;

    }

    @GetMapping("/{productId}")
    @ResponseBody
    public VxPublicEventProduct get(Authentication auth, @PathVariable Long productId) {
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);
        VxPublicEventProduct vxPublicEventProduct = VxDsService.getById(VxPublicEventProduct.class, systemService.getVxBankDatastore(), productId);
        return vxPublicEventProduct;
    }
}
