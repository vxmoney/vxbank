package eu.vxbank.api.endpoints.stripe;

import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.services.VxFirebaseAuthService;
import eu.vxbank.api.utils.components.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.service.VxService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/stripeConfig")
public class StripeConfigEndpoint {

    @Autowired
    private VxFirebaseAuthService vxFirebaseAuthService;

    @Autowired
    SystemService systemService;

    @GetMapping("/getByUserId/{userId}")
    @ResponseBody
    public StripeConfigGetByUserIdResponse getByUserId(@PathVariable("userId") Long userId) {

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> stripeConfigs = VxService.getByUserId(userId, new HashMap<>(), ds, VxStripeConfig.class);

        StripeConfigGetByUserIdResponse response = new StripeConfigGetByUserIdResponse();
        if (stripeConfigs.size() > 0) {
            VxStripeConfig config = stripeConfigs.get(0);
            response.state = config.state;
        } else {
            response.state = VxStripeConfig.State.notConfigured;
        }

        return response;
    }

    @PostMapping("/initiateConfig")
    @ResponseBody
    public StripeConfigInitiateConfigResponse initiateConfig(@RequestBody StripeConfigInitiateConfigParams params) {

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> stripeConfigs = VxService.getByUserId(params.userId,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);

        StripeConfigInitiateConfigResponse response = new StripeConfigInitiateConfigResponse();
        if (stripeConfigs.size() > 0) {
            throw new IllegalStateException("User already has a stripe config");
        }


        throw new IllegalStateException("Please implement this");
    }

}
