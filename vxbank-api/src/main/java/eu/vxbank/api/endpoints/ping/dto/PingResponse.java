package eu.vxbank.api.endpoints.ping.dto;

import eu.vxbank.api.ApplicationProps;
import eu.vxbank.api.utils.enums.Environment;

import javax.swing.plaf.PanelUI;

public class PingResponse {
    public Environment environment;
    public String projectId;
    public String message;
    public String testFirebaseIdToken;
    public Boolean activeFirebaseAuthEmulator;

    public ApplicationProps applicationProps;
    public String applicationEnvironment;

}
