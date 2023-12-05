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
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxService;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserEndpoint {

    @Autowired
    private VxFirebaseAuthService vxFirebaseAuthService;

    @Autowired
    SystemService systemService;

    private VxUser createNewUser(String email, VxBankDatastore ds) {
        VxUser vxUser = new VxUser();
        vxUser.email = email;
        VxUser persistedUser = VxService.persist(vxUser, ds, VxUser.class);
        return persistedUser;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginParams loginParams) throws FirebaseAuthException {

        String email = vxFirebaseAuthService.validateFirebaseIdTokenAndGetEmail(loginParams.firebaseIdToken);

        VxBankDatastore ds = systemService.getVxBankDatastore();
        Optional<VxUser> optionalUser = VxService.getUserByEmail(email, ds);
        if (optionalUser.isEmpty()) {
            VxUser user = createNewUser(email, ds);
            optionalUser = Optional.of(user);
        }

        VxUser vxUser = optionalUser.get();

        TokenInfo tokenInfo = vxFirebaseAuthService.buildTokenForUser(vxUser.id, vxUser.email, Optional.empty());


        UserResponse response = new UserResponse();
        response.id = vxUser.id;
        response.email = vxUser.email;
        response.message = "all good";
        response.vxToken = tokenInfo.vxToken;

        return response;
    }


}
