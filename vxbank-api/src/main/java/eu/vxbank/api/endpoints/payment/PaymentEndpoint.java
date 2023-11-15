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
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.models.VxServiceIntegration;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxService;

import java.util.*;

@RestController
public class PaymentEndpoint {

    @Autowired
    SystemService systemService;

    /**
     * This is deprecated and should not be used anymore
     */
    @Deprecated
    @PostMapping("/payments/create-payment-intent")
    @ResponseBody
    public StripeSessionCreateResponse createPaymentIntentIntent(
            @RequestBody DeprecatedCreatePaymentIntentParams createParams
    ) throws StripeException {

        Stripe.apiKey =
                "sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b";


        // Line item details
        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", "eur");
        Long timeStamp = new Date().getTime();
        priceData.put("product_data", Map.of("name", "IntegrationTest_" + timeStamp));
        priceData.put("unit_amount", 3000);

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price_data", priceData);
        lineItem.put("quantity", 1);

        // Line items list
        List<Object> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        // Session parameters
        Map<String, Object> params = new HashMap<>();
        params.put("line_items", lineItems);
        params.put("success_url", "http://localhost:3000/vxpayment/sucess?stripeSessionId=testSessionId&projectId=chessoutId&clubId=leuvenId&curencyId=eur&sessionValue=2500");
        params.put("cancel_url", "http://localhost:3000/vxpayment/cancel?stripeSessionId=testSessionId&projectId=chessoutId&clubId=leuvenId&curencyId=eur&sessionValue=2500");
        params.put("mode", "payment");

        Session session = Session.create(params);
        System.out.println("Checkout Session URL: " + session.getUrl());
        System.out.println("StripeSessionId = " + session.getId());

        StripeSessionCreateResponse stripeSessionResponse = new StripeSessionCreateResponse();
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();

        return stripeSessionResponse;
    }

    @PostMapping("/payment")
    @ResponseBody
    public StripeSessionCreateResponse create(
            @RequestBody PaymentCreateParams params
    ) throws StripeException {

        Stripe.apiKey =
                "sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b";

        VxBankDatastore ds = systemService.getVxBankDatastore();
        VxUser vxUser = VxService.get(params.vxUserId, VxUser.class, ds);
        VxServiceIntegration vxServiceIntegration = VxService.get(
                params.vxServiceIntegrationId,
                VxServiceIntegration.class,
                ds);
        VxPayment vxPayment = VxService.get(params.vxPaymentId, VxPayment.class, ds);

        StripeSessionCreateResponse stripeSessionCreateResponse =
                VxStripeUtil.createStripeSession(vxUser,vxServiceIntegration,vxPayment,Stripe.apiKey);

        return stripeSessionCreateResponse;
    }

}
