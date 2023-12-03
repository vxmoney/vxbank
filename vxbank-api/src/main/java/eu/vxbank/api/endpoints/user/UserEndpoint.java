package eu.vxbank.api.endpoints.user;


import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.internal.FirebaseService;
import eu.vxbank.api.endpoints.user.dto.LoginParams;
import eu.vxbank.api.endpoints.user.dto.TokenInfo;
import eu.vxbank.api.endpoints.user.dto.UserResponse;
import eu.vxbank.api.services.VxFirebaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserEndpoint {

    @Autowired
    private VxFirebaseAuthService vxFirebaseAuthService;

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginParams loginParams) throws FirebaseAuthException {

        TokenInfo tokenInfo = vxFirebaseAuthService.validateFirebaseToken(loginParams.firebaseToken);

        UserResponse response = new UserResponse();
        response.email = tokenInfo.email;
        response.message = "all good";
        response.vxToken = tokenInfo.vxToken;

        return response;
    }

}
