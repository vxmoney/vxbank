package eu.vxbank.api.utils.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import eu.vxbank.api.endpoints.payment.dto.DeprecatedStripeSessionResponse;
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.models.VxServiceIntegration;
import vxbank.datastore.data.models.VxUser;

import java.util.*;

public class VxStripeUtil {
    public static void createStripeSession(VxUser vxUser, VxServiceIntegration vxServiceIntegration, VxPayment vxPayment,
                                           String stripeApiKey) throws StripeException {

        // Line item details
        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", vxPayment.currency);
        Long timeStamp = new Date().getTime();
        priceData.put("product_data", Map.of("name", vxPayment.productName));
        priceData.put("unit_amount", vxPayment.valuePayedByUser);

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

        DeprecatedStripeSessionResponse stripeSessionResponse = new DeprecatedStripeSessionResponse();
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();
    }
}
