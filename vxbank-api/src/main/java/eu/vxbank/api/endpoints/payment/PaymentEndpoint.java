package eu.vxbank.api.endpoints.payment;


import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.payment.dto.PaymentCreateParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatParams;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionCreateResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.service.VxDsService;

@RestController
public class PaymentEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @PostMapping("/example/payment")
    @ResponseBody
    public StripeSessionCreateResponse create(
            @RequestBody PaymentCreateParams params
    ) throws StripeException {

        VxBankDatastore ds = systemService.getVxBankDatastore();

        VxPayment vxPayment = VxDsService.get(params.vxPaymentId, VxPayment.class, ds);

        StripeSessionCreateResponse stripeSessionCreateResponse =
                VxStripeUtil.createStripeSession(vxPayment, stripeKeys.stripeSecretKey);

        return stripeSessionCreateResponse;
    }

    @PostMapping("/payment/depositFiat")
    @ResponseBody
    public StripeSessionCreateResponse depositFiat(
            @RequestBody PaymentDepositFiatParams params
    ) throws StripeException {

        throw new IllegalStateException("Please implement this");
    }



}
