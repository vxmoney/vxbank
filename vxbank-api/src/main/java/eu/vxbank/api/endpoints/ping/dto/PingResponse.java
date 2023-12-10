package eu.vxbank.api.endpoints.ping.dto;

import eu.vxbank.api.utils.enums.Environment;

public class PingResponse {
    public Environment environment;
    public String projectId;
    public String message;
    public String testFirebaseIdToken;
    public Boolean activeFirebaseAuthEmulator;

}
