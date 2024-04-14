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
import vxbank.datastore.data.publicevent.VxPublicEventProduct;
import vxbank.datastore.data.publicevent.VxPublicEventSellingPoint;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;

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

        List<VxPublicEventProduct> productList = VxDsService.getByIdList(
                systemService.getVxBankDatastore(),
                VxPublicEventProduct.class,
                params.productIdList        );
        List<Long> productIdList = productList.stream().map(VxPublicEventProduct::getId).toList();

        VxPublicEventSellingPoint vxSellingPoint = VxPublicEventSellingPoint.builder()
                .vxPublicEventId(params.vxPublicEventId)
                .title(params.title)
                .productIdList(productIdList)
                .build();


        VxDsService.persist(VxPublicEventSellingPoint.class, systemService.getVxBankDatastore(), vxSellingPoint);
        SellingPointCreateResponse response = SellingPointCreateResponse.builder()
                .id(vxSellingPoint.getId())
                .vxPublicEventId(vxSellingPoint.getVxPublicEventId())
                .title(vxSellingPoint.getTitle())
                .productList(productList)
                .build();

       return response;

    }
}
