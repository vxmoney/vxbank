package eu.vxbank.api.endpoints.stripe;

import eu.vxbank.api.endpoints.stripe.dto.StripeConfigGetByUserIdResponse;
import eu.vxbank.api.services.VxFirebaseAuthService;
import eu.vxbank.api.utils.components.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class StripeConfigEndpoint {

    @Autowired
    private VxFirebaseAuthService vxFirebaseAuthService;

    @Autowired
    SystemService systemService;

    @GetMapping("/getByUserId/{userId}")
    public StripeConfigGetByUserIdResponse getByUserId(@PathVariable("userId") Long userId) {

        StripeConfigGetByUserIdResponse response = new StripeConfigGetByUserIdResponse();
        response.state = null;

        throw new IllegalStateException("Please implement this");
    }

}
