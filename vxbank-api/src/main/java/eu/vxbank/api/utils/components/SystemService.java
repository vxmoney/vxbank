package eu.vxbank.api.utils.components;


import eu.vxbank.api.utils.ApiConstants;
import eu.vxbank.api.utils.enums.Environment;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxBankDatastore.ConnectionType;

import java.io.InputStream;
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
            case ApiConstants.APPLICATION_ID_DEVELOPMENT:
                environment = Environment.DEVELOPMENT;
                break;
            default:
                throw new IllegalStateException("Not supported projectId=" + projectId);
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
            default:
                throw new IllegalStateException("Not supported credentials for env: " + environment);
        }
    }


}

