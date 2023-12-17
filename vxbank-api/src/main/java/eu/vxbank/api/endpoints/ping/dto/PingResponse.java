package eu.vxbank.api.endpoints.ping.dto;

import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import eu.vxbank.api.utils.enums.Environment;
import vxbank.datastore.data.models.VxExampleModel;

public class PingResponse {
    public Environment environment;
    public String projectId;
    public String message;
    public String testFirebaseIdToken;
    public Boolean activeFirebaseAuthEmulator;

    public String applicationEnvironment;

    public VxIntegrationConfig vxIntegrationConfig;
    public VxExampleModel datastoreExampleMode;

}
