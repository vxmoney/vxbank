package eu.vxbank.api.endpoints.user;


import eu.vxbank.api.endpoints.user.dto.LoginParams;
import eu.vxbank.api.endpoints.user.dto.UserResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserEndpoint {

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginParams loginParams){
        UserResponse response = new UserResponse();
        response.id = 100L;
        response.email = loginParams.firebaseToken;
        return response;
    }

}
