package eu.vxbank.api.endpoints.user;


import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.user.dto.Funds;
import eu.vxbank.api.endpoints.user.dto.LoginParams;
import eu.vxbank.api.endpoints.user.dto.TokenInfo;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.services.VxFirebaseAuthService;
import eu.vxbank.api.services.dao.ValidateFirebaseResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserEndpoint {

    @Autowired
    private VxFirebaseAuthService vxFirebaseAuthService;

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    private VxUser createNewUser(String email, String name, VxBankDatastore ds) {
        VxUser vxUser = new VxUser();
        vxUser.email = email;
        vxUser.name = name;
        VxUser persistedUser = VxDsService.persist(vxUser, ds, VxUser.class);
        return persistedUser;
    }

    /**
     * Checks the firebase token and if there is no user with the sed email then it creates it.
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginParams loginParams) throws FirebaseAuthException {

        ValidateFirebaseResponse validateResponse =
                vxFirebaseAuthService.validateFirebaseIdTokenAndGetData(loginParams.firebaseIdToken);

        String email = validateResponse.email;
        VxBankDatastore ds = systemService.getVxBankDatastore();
        Optional<VxUser> optionalUser = VxDsService.getUserByEmail(email, ds);
        if (optionalUser.isEmpty()) {
            String name = validateResponse.name;
            VxUser user = createNewUser(email, name, ds);
            optionalUser = Optional.of(user);
        }

        VxUser vxUser = optionalUser.get();

        TokenInfo tokenInfo = vxFirebaseAuthService.buildTokenForUser(vxUser.id, vxUser.email, Optional.empty());


        LoginResponse response = new LoginResponse();
        response.id = vxUser.id;
        response.email = vxUser.email;
        response.name = vxUser.name;
        response.message = "all good";
        response.vxToken = tokenInfo.vxToken;


        return response;
    }

    @GetMapping("/refreshVxToken")
    @ResponseBody
    public LoginResponse refreshVxToken(Authentication authentication) throws StripeException {
        VxUser vxUser = systemService.validateAndGetUser(authentication);
        TokenInfo tokenInfo = vxFirebaseAuthService.buildTokenForUser(vxUser.id, vxUser.email, Optional.empty());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = vxUser.id;
        loginResponse.email = vxUser.email;
        loginResponse.vxTokenExpiresAt = tokenInfo.expiresAt;


        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> configList = VxDsService.getByUserId(loginResponse.id,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);
        if (!configList.isEmpty()) {
            VxStripeConfig config = configList.get(0);
            loginResponse.stripeConfigState = config.state;

            List<Funds> availableFunds = VxStripeUtil.getFundsList(stripeKeys.stripeSecretKey, config.stripeAccountId);
            loginResponse.availableFundsList = availableFunds;
        }

        return loginResponse;
    }


}
