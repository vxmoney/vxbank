package eu.vxbank.api.utils.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionCreateResponse;
import vxbank.datastore.data.models.VxIntegration;
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.models.VxUser;

import java.util.*;

public class VxStripeUtil {
    public static StripeSessionCreateResponse createStripeSession(VxUser vxUser,
                                                                  VxIntegration vxServiceIntegration,
                                                                  VxPayment vxPayment,
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

        String successUrl = String.format("http://localhost:3000/vxpayment/sucess?paymentId=%s", vxPayment.id);
        String cancelUrl = String.format("http://localhost:3000/vxpayment/cancel?paymentId=%s", vxPayment.id);

        // Session parameters
        Map<String, Object> params = new HashMap<>();
        params.put("line_items", lineItems);
        params.put("success_url", successUrl);
        params.put("cancel_url", cancelUrl);
        params.put("mode", "payment");

        Session session = Session.create(params);
        System.out.println("Checkout Session URL: " + session.getUrl());
        System.out.println("StripeSessionId = " + session.getId());
        System.out.println("paymentId");

        StripeSessionCreateResponse stripeSessionResponse = new StripeSessionCreateResponse();
        stripeSessionResponse.vxPaymentId = vxPayment.id;
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();
        return stripeSessionResponse;
    }

    public static Account createExpressAccount(String stripeKey) throws StripeException {
        Stripe.apiKey = stripeKey;
        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .build();
        Account account = Account.create(params);
        return account;
    }

    public static AccountLink createAccountLink(String stripeKey, String connectedAccountId) throws StripeException {
        Stripe.apiKey = stripeKey;

        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                .setAccount(connectedAccountId)
                .setRefreshUrl("https://example.com/reauth")
                .setReturnUrl("https://example.com/return")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(params);
        return accountLink;
    }

    public static Transfer sendFundsToStripeAccount(String stripeKey, String stripeAccountId, Long amount, String currency) throws StripeException {
        Stripe.apiKey = stripeKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("destination", stripeAccountId);
        Transfer transfer = Transfer.create(params);
        return transfer;
    }
}
