package eu.vxbank.api.endpoints.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigFinalizeConfigResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.services.VxFirebaseAuthService;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.service.VxDsService;

import java.util.HashMap;
import java.util.List;

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
    public StripeConfigGetByUserIdResponse getByUserId(@PathVariable("userId") Long userId,
                                                       Authentication authentication) {

        // check security
        Long authId = Long.valueOf(authentication.getName());
        if (!authId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are trying to initiate configuration of someone else");
        }

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> stripeConfigs = VxDsService.getByUserId(userId, new HashMap<>(), ds, VxStripeConfig.class);

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
    public StripeConfigInitiateConfigResponse initiateConfig(@RequestBody StripeConfigInitiateConfigParams params,
                                                             Authentication authentication) throws StripeException {

        // check security
        Long authId = Long.valueOf(authentication.getName());
        if (!authId.equals(params.userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are trying to initiate configuration of someone else");
        }


        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> stripeConfigs = VxDsService.getByUserId(params.userId,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);

        StripeConfigInitiateConfigResponse response = new StripeConfigInitiateConfigResponse();

        String stripeKey = stripeKeys.stripeSecretKey;
        String refreshRedirectUrl = systemService.getStripeRefreshRedirectUrl();
        if (stripeConfigs.isEmpty()) {

            // create first config
            Account account = VxStripeUtil.createExpressAccount(stripeKey);

            String stripeAccountId = account.getId();

            AccountLink accountLink = VxStripeUtil.createAccountLink(stripeKey, stripeAccountId, refreshRedirectUrl);

            //create stripe config in progress
            VxStripeConfig config = VxStripeConfig.builder()
                    .userId(params.userId)
                    .stripeAccountId(stripeAccountId)
                    .state(VxStripeConfig.State.configurationInProgress)
                    .build();
            VxDsService.persist(config, ds, VxStripeConfig.class);


            StripeConfigInitiateConfigResponse initiateConfigResponse = new StripeConfigInitiateConfigResponse();
            initiateConfigResponse.expiresAt = accountLink.getExpiresAt();
            initiateConfigResponse.url = accountLink.getUrl();
            initiateConfigResponse.userId = params.userId;
            initiateConfigResponse.stripeAccountId = config.stripeAccountId;
            initiateConfigResponse.state = config.state;
            return initiateConfigResponse;

        } else {
            // just create new link so frontend can try again
            VxStripeConfig config = stripeConfigs.get(0);
            if (config.state != VxStripeConfig.State.configurationInProgress) {
                throw new IllegalStateException("You can try this only if configuration is in progress");
            }

            Stripe.apiKey = stripeKey;
            Account account = Account.retrieve(config.stripeAccountId);

            List<String> currentlyDueList = account.getRequirements()
                    .getCurrentlyDue();
            if (currentlyDueList.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "We need to clarify our onboarding flow. " +
                                "This account configuration is complete. No need to initiate configuration");

            }

            AccountLink accountLink = VxStripeUtil.createAccountLink(stripeKey, config.stripeAccountId,
                    refreshRedirectUrl);
            StripeConfigInitiateConfigResponse initiateConfigResponse = new StripeConfigInitiateConfigResponse();
            initiateConfigResponse.expiresAt = accountLink.getExpiresAt();
            initiateConfigResponse.url = accountLink.getUrl();
            initiateConfigResponse.userId = params.userId;
            initiateConfigResponse.stripeAccountId = config.stripeAccountId;
            initiateConfigResponse.state = config.state;
            return initiateConfigResponse;
        }
    }


    @PostMapping("/finalizeConfig")
    @ResponseBody
    public StripeConfigFinalizeConfigResponse finalizeConfig(@RequestBody StripeConfigInitiateConfigParams params,
                                                             Authentication authentication) throws
            StripeException {

        // check security
        Long authId = Long.valueOf(authentication.getName());
        if (!authId.equals(params.userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are trying to initiate configuration of someone else");
        }

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> stripeConfigs = VxDsService.getByUserId(params.userId,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);
        if (stripeConfigs.isEmpty()) {
            throw new IllegalStateException("Configuration was not initiated");
        }
        VxStripeConfig config = stripeConfigs.get(0);
        if (config.state == VxStripeConfig.State.active) {
            throw new IllegalStateException("Configuration is already active");
        }
        if (config.state == VxStripeConfig.State.restricted) {
            throw new IllegalStateException("Contact vxBank. This stripe configuration is restricted");
        }
        if (config.state != VxStripeConfig.State.configurationInProgress) {
            throw new IllegalStateException("Contact vxBank. Configuration state = " + config.state);
        }

        Stripe.apiKey = stripeKeys.stripeSecretKey;
        Account account = Account.retrieve(config.stripeAccountId);

        List<String> currentlyDueList = account.getRequirements()
                .getCurrentlyDue();
        if (!currentlyDueList.isEmpty()) {
            throw new IllegalStateException("Stripe still contains currentlyDueList: " + currentlyDueList);
        }

        config.state = VxStripeConfig.State.active;
        VxStripeConfig updatedConfig = VxDsService.persist(config, ds, VxStripeConfig.class);

        StripeConfigFinalizeConfigResponse finalizeConfigResponse = new StripeConfigFinalizeConfigResponse();
        finalizeConfigResponse.userId = updatedConfig.userId;
        finalizeConfigResponse.stripeAccountId = updatedConfig.stripeAccountId;
        finalizeConfigResponse.state = updatedConfig.state;

        return finalizeConfigResponse;
    }

}
