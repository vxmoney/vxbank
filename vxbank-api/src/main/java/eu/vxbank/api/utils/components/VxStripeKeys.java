package eu.vxbank.api.utils.components;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VxStripeKeys {
    @Autowired
    private SystemService systemService;
    public String secretKey;
    public String webhookSigningSecret;


    @PostConstruct
    public void init() {
        switch (systemService.environment) {
            case LOCALHOST:
            case DEVELOPMENT:
                this.secretKey =
                        "sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b";
                this.webhookSigningSecret = "whsec_J71Mv8Nl89K2iCgaXjmOXazVlktirOPv";
                return;
            default:
                throw new IllegalStateException("Not supported environment: " + systemService.environment);

        }
    }

}
