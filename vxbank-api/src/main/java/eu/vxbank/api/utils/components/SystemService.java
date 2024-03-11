package eu.vxbank.api.utils.components;


import eu.vxbank.api.utils.ApiConstants;
import eu.vxbank.api.utils.enums.Environment;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxBankDatastore.ConnectionType;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Getter
public class SystemService {

    @Value("${system.environment}")
    String applicationEnvironment;


    Environment environment;
    String projectId;
    VxBankDatastore vxBankDatastore;
    Boolean activeFirebaseAuthEmulator;


    @PostConstruct
    public void init() {
        initEnvironment();
        initDatastore();
        detectFirebaseAuthEmulatorState();
        System.out.println("Initialized SystemService");
    }

    private void detectFirebaseAuthEmulatorState() {
        Optional<String> optionalEmulator = Optional.ofNullable(System.getenv("FIREBASE_AUTH_EMULATOR_HOST"));
        if (optionalEmulator.isPresent()) {
            activeFirebaseAuthEmulator = true;
        } else {
            activeFirebaseAuthEmulator = false;
        }
    }


    private void initEnvironment() {
        projectId = System.getenv()
                .getOrDefault(ApiConstants.GAE_APPLICATION, ApiConstants.APPLICATION_ID_LOCALHOST);

        switch (projectId) {
            case ApiConstants.APPLICATION_ID_LOCALHOST:
                environment = Environment.LOCALHOST;
                break;
            case "e~vxbank-eu-dev": // just for testing
                // override projectId we connect datastore to actual development datastore
                projectId = ApiConstants.APPLICATION_ID_DEVELOPMENT;
                environment = Environment.DEVELOPMENT;
                break;
            case ApiConstants.APPLICATION_ID_DEVELOPMENT:
                environment = Environment.DEVELOPMENT;
                break;
            case "e~vxbank-eu-prod":
                projectId = ApiConstants.APPLICATION_ID_PRODUCTION;
                environment = Environment.PRODUCTION;
                break;
            case ApiConstants.APPLICATION_ID_PRODUCTION:
                environment = Environment.PRODUCTION;
                break;
            default:
                throw new IllegalStateException("SystemService.initEnvironment Not supported projectId=" + projectId);
        }
    }

    private void initDatastore() {
        switch (environment) {
            case LOCALHOST:
                VxBankDatastore localDatastore = new VxBankDatastore();
                ConnectionType connectionType = ConnectionType.localhost;
                vxBankDatastore = VxBankDatastore.init("my-project", ConnectionType.localhost, Optional.empty());
                break;
            case DEVELOPMENT:
                InputStream credentialsStream = getDatastoreCredentialsInputStream();
                VxBankDatastore devDatastore = new VxBankDatastore();
                vxBankDatastore = VxBankDatastore.init(projectId,
                        ConnectionType.connectedToAppEngine,
                        Optional.of(credentialsStream));
                break;
            case PRODUCTION:
                InputStream productionStream = getDatastoreCredentialsInputStream();
                VxBankDatastore prodDatastore = new VxBankDatastore();
                vxBankDatastore = VxBankDatastore.init(projectId,
                        ConnectionType.connectedToAppEngine,
                        Optional.of(productionStream));
                break;
            default:
                throw new IllegalStateException("Not supported initDatastore for env: " + environment);
        }
    }


    private InputStream getDatastoreCredentialsInputStream() {
        System.out.println("getDatastoreCredentialsInputStream projectId=" + projectId);
        switch (environment) {
            case DEVELOPMENT:
                return getClass().getClassLoader()
                        .getResourceAsStream("vxbank-eu-dev-key.json");
            case PRODUCTION:
                return getClass().getClassLoader()
                        .getResourceAsStream("vxbank-eu-prod-key.json");
            default:
                throw new IllegalStateException(
                        "Not supported getDatastoreCredentialsInputStream for env: " + environment);
        }
    }


    public VxUser validateUserAndStripeConfig(Authentication auth) {
        Jwt jwtToken = (Jwt) auth.getPrincipal();
        String email = jwtToken.getClaim("email");
        Long userId = Long.valueOf(auth.getName());

        Optional<VxUser> optionalVxUser = VxDsService.getUserByEmail(email, vxBankDatastore);
        if (optionalVxUser.isEmpty()) {
            throw new IllegalStateException("Not able to locateUser by email " + email);
        }

        VxUser vxUser = optionalVxUser.get();


        List<VxStripeConfig> configList = VxDsService.getByUserId(userId,
                new HashMap<>(),
                vxBankDatastore,
                VxStripeConfig.class);
        if (configList.isEmpty()) {
            throw new IllegalStateException("No stripe config for userId " + userId);
        }
        if (configList.size() != 1) {
            throw new IllegalStateException("We found multiple configuration for userId " + userId);
        }
        VxStripeConfig stripeConfig = configList.get(0);
        if (stripeConfig.state != VxStripeConfig.State.active) {
            throw new IllegalStateException(
                    "Illegal stripe config state for userId = " + userId + " state=" + stripeConfig.state);
        }

        return vxUser;
    }

    public VxUser validateAndGetUser(Authentication auth){
        Jwt jwtToken = (Jwt) auth.getPrincipal();
        String email = jwtToken.getClaim("email");
        Long userId = Long.valueOf(auth.getName());

        Optional<VxUser> optionalVxUser = VxDsService.getUserByEmail(email, vxBankDatastore);
        if (optionalVxUser.isEmpty()) {
            throw new IllegalStateException("Not able to locateUser by email " + email);
        }

        VxUser vxUser = optionalVxUser.get();
        if (optionalVxUser.isEmpty()) {
            throw new IllegalStateException("Not able to locateUser by email " + email);
        }
        return vxUser;
    }

    public String getStripeRefreshRedirectUrl() {
        switch (environment) {
            case LOCALHOST -> {
                return "http://localhost:3000/profile";
            }
            case DEVELOPMENT -> {
                return "https://vxbank-eu-dev.ew.r.appspot.com/profile";
            }
            default -> throw new IllegalStateException("getStripeRefreshRedirectUrl Not yet available in production");

        }
    }

    public String getStripeCancelRedirectUrl() {
        switch (environment) {
            case LOCALHOST -> {
                return "http://localhost:3000/cancel";
            }
            case DEVELOPMENT -> {
                return "https://vxbank-eu-dev.ew.r.appspot.com/cancel";
            }
            default -> throw new IllegalStateException("getStripeCancelRedirectUrl Not yet available in production");

        }
    }
}

