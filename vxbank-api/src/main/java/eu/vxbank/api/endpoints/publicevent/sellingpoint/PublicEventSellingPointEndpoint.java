package eu.vxbank.api.endpoints.publicevent.sellingpoint;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.product.dto.ProductSearchResponse;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointParams;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointResponse;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointSearchResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEvent;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;
import vxbank.datastore.data.publicevent.VxPublicEventSellingPoint;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public SellingPointResponse create(Authentication auth, @RequestBody SellingPointParams params) throws
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
        SellingPointResponse response = SellingPointResponse.builder()
                .id(vxSellingPoint.getId())
                .vxPublicEventId(vxSellingPoint.getVxPublicEventId())
                .title(vxSellingPoint.getTitle())
                .productList(productList)
                .build();

       return response;

    }

    @GetMapping("/{pointId}")
    @ResponseBody
    public SellingPointResponse get(Authentication auth, @PathVariable Long pointId) {
        systemService.validateUserAndStripeConfig(auth);
        VxPublicEventSellingPoint vxPublicEventSellingPoint = VxDsService.getById(VxPublicEventSellingPoint.class,
                systemService.getVxBankDatastore(), pointId);

        SellingPointResponse response = buildResponse(vxPublicEventSellingPoint);
        return response;
    }

    @PutMapping("/{pointId}")
    public SellingPointResponse update(Authentication auth, @PathVariable Long pointId, @RequestBody SellingPointParams params) {
        VxUser currentUser = systemService.validateAndGetUser(auth);
        VxPublicEventSellingPoint vxPublicEventSellingPoint = VxDsService.getById(VxPublicEventSellingPoint.class, systemService.getVxBankDatastore(), pointId);
        VxPublicEvent vxEvent = getVxEvent(systemService.getVxBankDatastore(), vxPublicEventSellingPoint.vxPublicEventId);
        checkUserIsOwnerOfEvent(currentUser, vxEvent);

        vxPublicEventSellingPoint = vxPublicEventSellingPoint.toBuilder()
                .title(params.title)
                .productIdList(params.productIdList)
                .build();


        VxDsService.persist(VxPublicEventSellingPoint.class, systemService.getVxBankDatastore(), vxPublicEventSellingPoint);

        SellingPointResponse response = buildResponse(vxPublicEventSellingPoint);

        return response;
    }

    private SellingPointResponse buildResponse(VxPublicEventSellingPoint vxPublicEventSellingPoint) {
        List<VxPublicEventProduct> productList = VxDsService.getByIdList(
                systemService.getVxBankDatastore(),
                VxPublicEventProduct.class,
                vxPublicEventSellingPoint.getProductIdList()
        );

        SellingPointResponse response = SellingPointResponse.builder()
                .id(vxPublicEventSellingPoint.getId())
                .vxPublicEventId(vxPublicEventSellingPoint.getVxPublicEventId())
                .title(vxPublicEventSellingPoint.getTitle())
                .productList(productList)
                .build();
        return response;
    }

    private SellingPointResponse buildResponseFromLoadedData(VxPublicEventSellingPoint vxPublicEventSellingPoint,
                                                             Map<Long, VxPublicEventProduct> allProductsMap ) {
        List<VxPublicEventProduct> productList = vxPublicEventSellingPoint.getProductIdList().stream()
                .map(allProductsMap::get)
                .collect(Collectors.toList());

        SellingPointResponse response = SellingPointResponse.builder()
                .id(vxPublicEventSellingPoint.getId())
                .vxPublicEventId(vxPublicEventSellingPoint.getVxPublicEventId())
                .title(vxPublicEventSellingPoint.getTitle())
                .productList(productList)
                .build();
        return response;
    }

    @GetMapping
    @ResponseBody
    public SellingPointSearchResponse search(Authentication auth, @RequestParam(name = "publicEventId") Long publicEventId) {

        VxUser vxUser = systemService.validateAndGetUser(auth);

        List<VxPublicEventSellingPoint> sellingPointList = VxDsService.getByVxPublicEventId(VxPublicEventSellingPoint.class,
                systemService.getVxBankDatastore(), publicEventId);

        List<VxPublicEventProduct> productList = VxDsService.getByVxPublicEventId(VxPublicEventProduct.class,
                systemService.getVxBankDatastore(), publicEventId);

        Map<Long, VxPublicEventProduct> publicEventProductMap = productList.stream()
                .collect(Collectors.toMap(product -> product.id, product -> product));

        SellingPointSearchResponse response = new SellingPointSearchResponse();
        response.totalCount = sellingPointList.size();
        response.sellingPointList = sellingPointList.stream().map(
                point -> buildResponseFromLoadedData(point, publicEventProductMap)
        ).toList();

        return response;

    }
}
