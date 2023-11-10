package eu.vxbank.api.utils.components;

import eu.vxbank.api.utils.enums.Environment;
import eu.vxbank.api.utils.stripe.VxStripe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FxStripeService {

    @Autowired
    SystemService systemService;
    VxStripe vxStripe;

    public void init() {
        this.vxStripe = new VxStripe();
        vxStripe.init("fake-publickey", "fake-privatekey");
    }

}
