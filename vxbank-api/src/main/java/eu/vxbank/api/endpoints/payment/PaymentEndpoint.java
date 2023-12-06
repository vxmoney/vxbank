package eu.vxbank.api.endpoints.payment;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;


import eu.vxbank.api.endpoints.payment.dto.DeprecatedCreatePaymentIntentParams;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionCreateResponse;
import eu.vxbank.api.endpoints.payment.dto.PaymentCreateParams;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxIntegration;
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxService;

import java.util.*;

@RestController
public class PaymentEndpoint {

    @Autowired
    SystemService systemService;



    @PostMapping("/example/payment")
    @ResponseBody
    public StripeSessionCreateResponse create(
            @RequestBody PaymentCreateParams params
    ) throws StripeException {

        Stripe.apiKey =
                "sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b";

        VxBankDatastore ds = systemService.getVxBankDatastore();
        VxUser vxUser = VxService.get(params.vxUserId, VxUser.class, ds);
        VxIntegration vxServiceIntegration = VxService.get(
                params.vxServiceIntegrationId,
                VxIntegration.class,
                ds);
        VxPayment vxPayment = VxService.get(params.vxPaymentId, VxPayment.class, ds);

        StripeSessionCreateResponse stripeSessionCreateResponse =
                VxStripeUtil.createStripeSession(vxUser,vxServiceIntegration,vxPayment,Stripe.apiKey);

        return stripeSessionCreateResponse;
    }

}
