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
                this.webhookSigningSecret = "whsec_b36f59fd7556a24cbdd59589110a616aebb7a35167d04d2aade484c8a345af53";
                return;
            default:
                throw new IllegalStateException("Not supported environment: " + systemService.environment);

        }
    }

}
