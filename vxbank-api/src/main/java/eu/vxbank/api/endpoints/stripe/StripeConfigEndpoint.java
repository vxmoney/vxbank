package eu.vxbank.api.endpoints.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.services.VxFirebaseAuthService;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.service.VxService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stripeConfig")
public class StripeConfigEndpoint {

    @Autowired
    private VxFirebaseAuthService vxFirebaseAuthService;

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

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
    public StripeConfigInitiateConfigResponse initiateConfig(@RequestBody StripeConfigInitiateConfigParams params) throws
            StripeException {

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> stripeConfigs = VxService.getByUserId(params.userId,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);

        StripeConfigInitiateConfigResponse response = new StripeConfigInitiateConfigResponse();

        String stripeKey = stripeKeys.stripeSecretKey;

        if (stripeConfigs.isEmpty()){

            // create first config
            Account account = VxStripeUtil.createExpressAccount(stripeKey);

            String stripeAccountId = account.getId();
            AccountLink accountLink = VxStripeUtil.createAccountLink(stripeKey,stripeAccountId);

            //create stripe config in progress
            VxStripeConfig config = VxStripeConfig.builder()
                    .userId(params.userId)
                    .stripeAccountId(stripeAccountId)
                    .state(VxStripeConfig.State.configurationInProgress)
                    .build();
            VxService.persist(config, ds, VxStripeConfig.class);


            StripeConfigInitiateConfigResponse initiateConfigResponse = new StripeConfigInitiateConfigResponse();
            initiateConfigResponse.expiresAt = accountLink.getExpiresAt();
            initiateConfigResponse.url = accountLink.getUrl();
            initiateConfigResponse.userId = params.userId;
            initiateConfigResponse.stripeAccountId = config.stripeAccountId;
            initiateConfigResponse.state = config.state;
            return initiateConfigResponse;

        }else {
            // just create new link so frontend can try again
            VxStripeConfig config = stripeConfigs.get(0);
            if (config.state != VxStripeConfig.State.configurationInProgress){
                throw new IllegalStateException("You can try this only if configuration is in progress");
            }

            Stripe.apiKey = stripeKey;
            Account account = Account.retrieve(config.stripeAccountId);



            AccountLink accountLink = VxStripeUtil.createAccountLink(stripeKey,config.stripeAccountId);
            StripeConfigInitiateConfigResponse initiateConfigResponse = new StripeConfigInitiateConfigResponse();
            initiateConfigResponse.expiresAt = accountLink.getExpiresAt();
            initiateConfigResponse.url = accountLink.getUrl();
            initiateConfigResponse.userId = params.userId;
            initiateConfigResponse.stripeAccountId = config.stripeAccountId;
            initiateConfigResponse.state = config.state;
            return initiateConfigResponse;
        }
    }

}
