package eu.vxbank.api.ping;

import eu.vxbank.api.endpoints.response.PingResponse;
import eu.vxbank.api.utils.components.SystemService;
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



}
