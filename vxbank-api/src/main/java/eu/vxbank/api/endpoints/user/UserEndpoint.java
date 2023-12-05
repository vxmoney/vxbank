package eu.vxbank.api.endpoints.user;


import com.google.firebase.auth.FirebaseAuthException;
import eu.vxbank.api.endpoints.user.dto.LoginParams;
import eu.vxbank.api.endpoints.user.dto.TokenInfo;
import eu.vxbank.api.endpoints.user.dto.UserResponse;
import eu.vxbank.api.services.VxFirebaseAuthService;
import eu.vxbank.api.utils.components.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.data.models.VxUser;

@RestController
@RequestMapping("/user")
public class UserEndpoint {

    @Autowired
    private VxFirebaseAuthService vxFirebaseAuthService;

    @Autowired
    SystemService systemService;

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginParams loginParams) throws FirebaseAuthException {

        String email = vxFirebaseAuthService.validateFirebaseIdTokenAndGetEmail(loginParams.firebaseIdToken);

        UserResponse response = new UserResponse();
        response.email = email;
        response.message = "all good";
        response.vxToken = "Not yet";

        return response;
    }

}
