package eu.vxbank.api.endpoints.ping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import eu.vxbank.api.endpoints.payment.dto.HandleCheckoutSessionCompletedDto;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionCreateResponse;
import eu.vxbank.api.endpoints.ping.dto.*;
import eu.vxbank.api.endpoints.user.dto.Funds;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import eu.vxbank.api.utils.enums.Environment;
import eu.vxbank.api.utils.queue.QueueUtil;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.*;
import vxbank.datastore.data.service.VxDsService;

import eu.vxbank.api.utils.stripe.VxStripeUtil;

import java.util.*;

@RestController
public class PingEndpoint {
    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @GetMapping("/ping/getEnvironment")
    @ResponseBody
    public PingResponse getEnvironment() {

        PingResponse pingResponse = new PingResponse();
        pingResponse.environment = systemService.getEnvironment();
        pingResponse.projectId = systemService.getProjectId();
        pingResponse.activeFirebaseAuthEmulator = systemService.getActiveFirebaseAuthEmulator();
        pingResponse.applicationEnvironment = systemService.getApplicationEnvironment();
        pingResponse.vxIntegrationConfig = vxIntegrationConfig;

        // datastore test
        long timeStamp = new Date().getTime();
        VxExampleModel vxExampleModel = new VxExampleModel();

        StringBuilder sb = new StringBuilder();
        sb.append("Env=" + pingResponse.environment + " ");
        sb.append(" timeStamp=" + timeStamp);
        vxExampleModel.description = sb.toString();
        VxDsService.persist(vxExampleModel, systemService.getVxBankDatastore(), VxExampleModel.class);

        pingResponse.datastoreExampleMode = vxExampleModel;

        //todo: remove this when you are done with debug queue
        HandleCheckoutSessionCompletedDto dto = new HandleCheckoutSessionCompletedDto();
        dto.payload = "Hello payload";
        dto.stripeSignature = "Hello signature";

        QueueUtil.pushToHandleCheckoutSessionCompleted(systemService, dto);

        return pingResponse;
    }


    // curl localhost:8080/ping/generateFirebaseIdToken
    @GetMapping("/ping/generateFirebaseIdToken")
    @ResponseBody
    public PingResponse generateFirebaseIdToken() throws FirebaseAuthException, JsonProcessingException {

        if (!systemService.getActiveFirebaseAuthEmulator()) {
            throw new IllegalStateException("You are allowed to do this only when oauth emulator is active");
        }

        if (FirebaseApp.getApps().isEmpty()) {
            // Firebase has not been initialized yet, so initialize it
            FirebaseApp.initializeApp();
        }


        if (systemService.getEnvironment() != Environment.LOCALHOST) {
            throw new IllegalStateException("We only do this on localhost");
        }

        String fireEmulatorEnv = System.getenv("FIREBASE_AUTH_EMULATOR_HOST");
        if (fireEmulatorEnv == null) {
            throw new IllegalStateException("FIREBASE_AUTH_EMULATOR_HOST is not set");
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        long timeStamp = (new Date()).getTime();
        String email = String.format("user_%s@example.com", timeStamp);
        String name = String.format("user_%s", timeStamp);


        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
                .setDisplayName(name)
                .setEmailVerified(false)
                .setPassword("secure-password"); // Set a secure password for the user

        UserRecord userRecord = firebaseAuth.createUser(request);
        String userId = userRecord.getUid();
        System.out.println("userId=" + userId);

        String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
        String idToken = swapOnLocalhostCustomTokenForIdToken(customToken);

        StringBuilder sb = new StringBuilder();
        sb.append("\nGENERATION COMPLETE\n");
        sb.append("You can view the user in console here: http://127.0.0.1:4000/auth\n");
        sb.append(String.format("export ID_TOKEN=%s", idToken));
        sb.append("\n");
        sb.append("""
                curl -X POST -H "Content-Type: application/json" -d '{"firebaseToken":"'${ID_TOKEN}'"}' http://localhost:8080/user/login
                """);
        sb.append(String.format(
                "curl -X POST -H \"Content-Type: application/json\" -d '{\"firebaseToken\":\"'%s'\"}' " +
                        "http://localhost:8080/user/login\n", idToken));

        PingResponse pingResponse = new PingResponse();
        pingResponse.environment = systemService.getEnvironment();
        pingResponse.projectId = systemService.getProjectId();
        pingResponse.message = sb.toString();
        pingResponse.testFirebaseIdToken = idToken;
        System.out.println(pingResponse.message);

        return pingResponse;
    }

    private String swapOnLocalhostCustomTokenForIdToken(String customToken) throws JsonProcessingException {

        HttpResponse<String> response = Unirest.post("http://localhost:9099/identitytoolkit.googleapis" +
                        ".com/v1/accounts:signInWithCustomToken?key=AIzaSyD3WNQhta9K8SW42PpGDnozZwm16vTJq8")
                .header("Content-Type", "application/json")
                .body("{\"token\":\"" + customToken + "\",\"returnSecureToken\":true}")
                .asString();

        String body = response.getBody();
        System.out.println(body);

        ObjectMapper objectMapper = new ObjectMapper();
        FirebaseSwapResponse oauthResponse = objectMapper.readValue(body, FirebaseSwapResponse.class);
        return oauthResponse.idToken;

    }

    @GetMapping("/ping/whoAmI")
    @ResponseBody
    public LoginResponse whoAmI(Authentication authentication) throws StripeException {


        Jwt jwtToken = (Jwt) authentication.getPrincipal();
        String email = jwtToken.getClaim("email");


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = Long.valueOf(authentication.getName());
        loginResponse.email = email;

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

    @PostMapping("/ping/requestFunds")
    public PingRequestFundsResponse requestFunds(Authentication auth, @RequestBody PingRequestFundsParams params) throws
            StripeException {


        if (systemService.getEnvironment() == Environment.PRODUCTION) {
            throw new IllegalStateException("You can not request funds in production");
        }

        List<Funds> platformFunds = VxStripeUtil.getPlatformFundsList(stripeKeys.stripeSecretKey);

        VxStripeConfig vxStripeConfig = VxDsService.getByUserId(params.userId,
                        new HashMap<>(),
                        systemService.getVxBankDatastore(),
                        VxStripeConfig.class)
                .get(0);

        Boolean clientCanReceivePaymentInCurrency =
                VxStripeUtil.clientCanReceivePaymentInCurrency(stripeKeys.stripeSecretKey,
                vxStripeConfig.stripeAccountId,
                params.currency);
        if (!clientCanReceivePaymentInCurrency) {
            throw new IllegalStateException("Client can not process respective currency. " + params.currency);
        }

        VxStripeUtil.sendFundsToStripeAccount(stripeKeys.stripeSecretKey,
                vxStripeConfig.stripeAccountId,
                params.amount,
                params.currency);

        PingRequestFundsResponse response = new PingRequestFundsResponse();
        response.userId = params.userId;

        List<Funds> fundsList = VxStripeUtil.getFundsList(stripeKeys.stripeSecretKey, vxStripeConfig.stripeAccountId);
        response.fundsList = fundsList;

        return response;
    }

    @GetMapping("/ping/initiateVxGaming")
    @ResponseBody
    public PingInitiateVxGamingResponse initiateVxGaming(Authentication authentication) throws StripeException {

        if (systemService.getEnvironment() == Environment.PRODUCTION) {
            throw new IllegalStateException("You can not request funds in production");
        }

        Jwt jwtToken = (Jwt) authentication.getPrincipal();
        String email = jwtToken.getClaim("email");


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = Long.valueOf(authentication.getName());
        loginResponse.email = email;

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> configList = VxDsService.getByUserId(loginResponse.id,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);


        StripeSessionCreateResponse stripeResponse = createStripeSessionInitiateVxGaming(stripeKeys.stripeSecretKey);


        PingInitiateVxGamingResponse response = new PingInitiateVxGamingResponse();
        response.payUrl = stripeResponse.url;


        return response;
    }

    private StripeSessionCreateResponse createStripeSessionInitiateVxGaming(String stripeKey) throws StripeException {

        if (systemService.getEnvironment() == Environment.PRODUCTION) {
            throw new IllegalStateException("You can not request funds in production");
        }

        Stripe.apiKey = stripeKey;

        // Line item details
        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", "eur");
        Long timeStamp = new Date().getTime();
        priceData.put("product_data", Map.of("name", "initiate vx gaming"));
        priceData.put("unit_amount", 10000000L);

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price_data", priceData);
        lineItem.put("quantity", 1);

        // Line items list
        List<Object> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        String successUrl = String.format("http://localhost:3000/vxpayment/sucess?paymentId=%s", "initiateVxGaming");
        String cancelUrl = String.format("http://localhost:3000/vxpayment/cancel?paymentId=%s", "initiateVxGaming");

        // Session parameters
        Map<String, Object> params = new HashMap<>();
        params.put("line_items", lineItems);
        params.put("success_url", successUrl);
        params.put("cancel_url", cancelUrl);
        params.put("mode", "payment");

        Session session = Session.create(params);
        System.out.println("Checkout Session URL: " + session.getUrl());
        System.out.println("StripeSessionId = " + session.getId());
        System.out.println("paymentId");

        StripeSessionCreateResponse stripeSessionResponse = new StripeSessionCreateResponse();
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();
        return stripeSessionResponse;
    }

    @PostMapping("/ping/initiateVxGamingCurrency")
    @ResponseBody
    public PingInitiateVxGamingResponse initiateVxGamingCurrency(Authentication authentication,
                                                                 @RequestBody InitiateVxGamingParams initiateVxGamingParams) throws
            StripeException {

        if (systemService.getEnvironment() == Environment.PRODUCTION) {
            throw new IllegalStateException("You can not request funds in production");
        }

        Jwt jwtToken = (Jwt) authentication.getPrincipal();
        String email = jwtToken.getClaim("email");


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = Long.valueOf(authentication.getName());
        loginResponse.email = email;

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> configList = VxDsService.getByUserId(loginResponse.id,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);


        StripeSessionCreateResponse stripeResponse =
                createStripeSessionInitiateVxGamingCurrency(stripeKeys.stripeSecretKey,
                initiateVxGamingParams);


        PingInitiateVxGamingResponse response = new PingInitiateVxGamingResponse();
        response.payUrl = stripeResponse.url;


        return response;
    }

    private StripeSessionCreateResponse createStripeSessionInitiateVxGamingCurrency(String stripeSecretKey,
                                                                                    InitiateVxGamingParams initiateVxGamingParams) throws
            StripeException {

        if (systemService.getEnvironment() == Environment.PRODUCTION) {
            throw new IllegalStateException("You can not request funds in production");
        }

        Stripe.apiKey = stripeSecretKey;

        // Line item details
        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", initiateVxGamingParams.currency);
        Long timeStamp = new Date().getTime();
        priceData.put("product_data", Map.of("name", "initiate vx gaming"));
        priceData.put("unit_amount", 10000000L);

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price_data", priceData);
        lineItem.put("quantity", 1);

        // Line items list
        List<Object> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        String successUrl = String.format("http://localhost:3000/vxpayment/sucess?paymentId=%s", "initiateVxGaming");
        String cancelUrl = String.format("http://localhost:3000/vxpayment/cancel?paymentId=%s", "initiateVxGaming");

        // Session parameters
        Map<String, Object> params = new HashMap<>();
        params.put("line_items", lineItems);
        params.put("success_url", successUrl);
        params.put("cancel_url", cancelUrl);
        params.put("mode", "payment");

        Session session = Session.create(params);
        System.out.println("Checkout Session URL: " + session.getUrl());
        System.out.println("StripeSessionId = " + session.getId());
        System.out.println("paymentId");

        StripeSessionCreateResponse stripeSessionResponse = new StripeSessionCreateResponse();
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();
        return stripeSessionResponse;
    }

}
