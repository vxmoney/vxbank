package eu.vxbank.api.endpoints.ping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import eu.vxbank.api.endpoints.ping.dto.FirebaseSwapResponse;
import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.enums.Environment;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class PingEndpoint {
    @Autowired
    SystemService systemService;

    @GetMapping("/ping/getEnvironment")
    @ResponseBody
    public PingResponse getEnvironment() {
        PingResponse pingResponse = new PingResponse();
        pingResponse.environment = systemService.getEnvironment();
        pingResponse.projectId = systemService.getProjectId();
        return pingResponse;
    }


    // curl localhost:8080/ping/generateFirebaseIdToken
    @GetMapping("/ping/generateFirebaseIdToken")
    @ResponseBody
    public PingResponse generateFirebaseIdToken() throws FirebaseAuthException, JsonProcessingException {

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

        System.out.println("EMIL = " + email);

        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(email)
                .setEmailVerified(false)
                .setPassword("secure-password"); // Set a secure password for the user

        UserRecord userRecord = firebaseAuth.createUser(request);
        String userId = userRecord.getUid();
        System.out.println("userId=" + userId);

        String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
        String idToken = swapOnLocalhostCustomTokenForIdToken(customToken);

        StringBuilder sb = new StringBuilder();
        sb.append("GENERATION COMPLETE\n");
        sb.append("You can view the user in console here: http://127.0.0.1:4000/auth\n");
        sb.append(String.format("export ID_TOKEN=%s", idToken));
        sb.append("\n");
        sb.append(
                "curl -X POST -H \"Content-Type: application/json\" -d '{\"firebaseToken\":\"'${ID_TOKEN}'\"}' " +
                        "http://localhost:8080/user/login\n");
        sb.append(
                String.format("curl -X POST -H \"Content-Type: application/json\" -d '{\"firebaseToken\":\"'%s'\"}' " +
                        "http://localhost:8080/user/login\n",idToken));

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

}
