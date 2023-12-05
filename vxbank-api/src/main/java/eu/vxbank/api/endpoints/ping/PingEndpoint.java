package eu.vxbank.api.endpoints.ping;

import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.enums.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingEndpoint {
    @Autowired
    SystemService systemService;

    @GetMapping("/ping/getEnvironment")
    @ResponseBody
    public PingResponse getEnvironment(){
        PingResponse pingResponse = new PingResponse();
        pingResponse.environment = systemService.getEnvironment();
        pingResponse.projectId = systemService.getProjectId();
        return pingResponse;
    }


    @GetMapping("/ping/generateFirebaseIdToken")
    public String generateTestToken(){
        if (systemService.getEnvironment() != Environment.LOCALHOST){
            throw new IllegalStateException("We only do this on localhost");
        }



        String fireEmulatorEnv = System.getenv("FIREBASE_AUTH_EMULATOR_HOST");
        if (fireEmulatorEnv == null){
            throw new IllegalStateException("FIREBASE_AUTH_EMULATOR_HOST is not set");
        }

        PingResponse pingResponse = new PingResponse();
        pingResponse.environment = systemService.getEnvironment();
        pingResponse.projectId = systemService.getProjectId();
        return "Generation complete";
    }

}
