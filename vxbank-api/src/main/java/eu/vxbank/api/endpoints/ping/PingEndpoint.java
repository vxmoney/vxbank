package eu.vxbank.api.endpoints.ping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import eu.vxbank.api.utils.components.vxintegration.ApplicationProps;
import eu.vxbank.api.endpoints.ping.dto.FirebaseSwapResponse;
import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import eu.vxbank.api.utils.enums.Environment;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.service.VxDsService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class PingEndpoint {
    @Autowired
    SystemService systemService;

    @Autowired
    private ApplicationProps applicationProps;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @GetMapping("/ping/getEnvironment")
    @ResponseBody
    public PingResponse getEnvironment() {
        PingResponse pingResponse = new PingResponse();
        pingResponse.environment = systemService.getEnvironment();
        pingResponse.projectId = systemService.getProjectId();
        pingResponse.activeFirebaseAuthEmulator = systemService.getActiveFirebaseAuthEmulator();
        pingResponse.applicationProps = applicationProps;
        pingResponse.applicationEnvironment = systemService.getApplicationEnvironment();
        pingResponse.vxIntegrationConfig = vxIntegrationConfig;
        return pingResponse;
    }


    // curl localhost:8080/ping/generateFirebaseIdToken
    @GetMapping("/ping/generateFirebaseIdToken")
    @ResponseBody
    public PingResponse generateFirebaseIdToken() throws FirebaseAuthException, JsonProcessingException {

        if (!systemService.getActiveFirebaseAuthEmulator()) {
            throw new IllegalStateException("You are allowed to do this only when oauth emulator is active");
        }

        try {
            FirebaseApp.initializeApp();
        } catch (Exception e) {
            System.out.println("Firebase was already initialized");
            // no need to initialize again
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


        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
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
    public LoginResponse whoAmI(Authentication authentication) {

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
        }

        return loginResponse;
    }


}
