package eu.vxbank.api.endpoints.user;


import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.stripe.dto.StripeCurrency;
import eu.vxbank.api.endpoints.user.dto.*;
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

import java.util.*;
import java.util.stream.Collectors;

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
    public LoginResponse login(@RequestBody LoginParams loginParams) throws FirebaseAuthException, StripeException {

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

        LoginResponse loginResponse = buildLoginResponse(vxUser);
        return loginResponse;
    }

    private LoginResponse buildLoginResponse(VxUser vxUser) throws StripeException {
        TokenInfo tokenInfo = vxFirebaseAuthService.buildTokenForUser(vxUser.id, vxUser.email, Optional.empty());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = vxUser.id;
        loginResponse.email = vxUser.email;
        loginResponse.name = vxUser.name;
        loginResponse.vxTokenExpiresAt = tokenInfo.expiresAt;
        loginResponse.vxToken = tokenInfo.vxToken;


        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> configList = VxDsService.getByUserId(loginResponse.id,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);
        if (!configList.isEmpty()) {
            VxStripeConfig config = configList.get(0);
            loginResponse.stripeConfigState = config.state;

            List<Funds> immutableFunds = VxStripeUtil.getFundsList(stripeKeys.stripeSecretKey, config.stripeAccountId);
            List<Funds> availableFunds = new ArrayList<>(immutableFunds);
            loginResponse.availableFundsList = availableFunds;

            //check add zero if eur are missing from that list.
            String stripeSecretKey = stripeKeys.stripeSecretKey;
            String userStripeAccountId = config.stripeAccountId;

            // check and eur if necessary
            Optional<Funds> optionalEuro = buildEmtpyFundsIfNotPresentAndUserCanProcessThem(stripeSecretKey,
                    userStripeAccountId,
                    StripeCurrency.eur,
                    availableFunds);
            optionalEuro.ifPresent(availableFunds::add);

            // check and add ron if necessary
            Optional<Funds> optionalRon = buildEmtpyFundsIfNotPresentAndUserCanProcessThem(stripeSecretKey,
                    userStripeAccountId,
                    StripeCurrency.ron,
                    availableFunds);
            optionalRon.ifPresent(availableFunds::add);
        }

        return loginResponse;
    }

    private Optional<Funds> buildEmtpyFundsIfNotPresentAndUserCanProcessThem(String stripeSecretKey,
                                                                             String userStripeAccountId,
                                                                             StripeCurrency currency,
                                                                             List<Funds> listToCheck) throws
            StripeException {
        // if currency already there then we do nothing
        Set<StripeCurrency> set = listToCheck.stream()
                .map(funds -> StripeCurrency.valueOf(funds.currency))
                .collect(Collectors.toSet());
        if (set.contains(currency)) {
            return Optional.empty();
        }

        // if user can not process currency we do noting
        Boolean clientCanReceivePaymentInCurrency = VxStripeUtil.clientCanReceivePaymentInCurrency(stripeSecretKey,
                userStripeAccountId,
                currency.toString());
        if (!clientCanReceivePaymentInCurrency) {
            return Optional.empty();
        }

        // client can process but no funds in the list. We just add zero funds.
        Funds zeroFunds = Funds.builder()
                .amount(0L)
                .currency(currency.toString())
                .build();
        return Optional.of(zeroFunds);
    }

    @GetMapping("/refreshVxToken")
    @ResponseBody
    public LoginResponse refreshVxToken(Authentication authentication) throws StripeException {
        VxUser vxUser = systemService.validateAndGetUser(authentication);
        LoginResponse response = buildLoginResponse(vxUser);
        return response;
    }

    @GetMapping("/getStripeLoginLink")
    @ResponseBody
    public UserStripeLinkResponse getStripeLoginLink(Authentication authentication) throws StripeException {
        VxUser vxUser = systemService.validateAndGetUser(authentication);

        VxStripeConfig vxStripeConfig = VxDsService.getByUserId(vxUser.id,
                        new HashMap<>(),
                        systemService.getVxBankDatastore(),
                        VxStripeConfig.class)
                .get(0);

        String stripeSecretKey = stripeKeys.stripeSecretKey;
        String stripeAccountId = vxStripeConfig.stripeAccountId;
        String uri = VxStripeUtil.createLoginLink(stripeSecretKey, stripeAccountId);
        UserStripeLinkResponse response = new UserStripeLinkResponse();
        response.uri = uri;
        return response;
    }


}
