package eu.vxbank.api.endpoints.payment;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentCreateParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatResponse;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionCreateResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.*;

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
    public PaymentDepositFiatResponse depositFiat(
            Authentication authentication,
            @RequestBody PaymentDepositFiatParams params
    ) throws StripeException {


        Jwt jwtToken = (Jwt) authentication.getPrincipal();
        String email = jwtToken.getClaim("email");


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = Long.valueOf(authentication.getName());
        loginResponse.email = email;

        VxBankDatastore ds = systemService.getVxBankDatastore();
        List<VxStripeConfig> configList = VxDsService.getByUserId(loginResponse.id,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);
        Optional<VxStripeConfig> optionalConfig = configList.stream().filter(
                        item -> item.currency.equals(params.currency))
                .findFirst();
        if (optionalConfig.isEmpty()) {
            throw new IllegalStateException("User needs to configure first bank for currency=" + params.currency);
        }

        VxStripeConfig stripeConfig = optionalConfig.get();
        VxUser user = VxDsService.getById(VxUser.class, ds, loginResponse.id);

        StripeSessionCreateResponse stripeSessionCreateResponse = createStripeSessionDepositFiatV2(
                stripeKeys.stripeSecretKey,
                user,
                stripeConfig,
                params);

        PaymentDepositFiatResponse response = new PaymentDepositFiatResponse();
        response.payUrl = stripeSessionCreateResponse.url;
        return response;
    }

    private StripeSessionCreateResponse createStripeSessionDepositFiat(String stripeSecretKey,
                                                                       VxUser user,
                                                                       VxStripeConfig userStripeConfig,
                                                                       PaymentDepositFiatParams depositFiatParams) throws
            StripeException {

        Stripe.apiKey = stripeSecretKey;

        Double _3procent = (3.0 * Double.valueOf(depositFiatParams.amount)) / 100.0;
        double messageValue = (Double.valueOf(depositFiatParams.amount) - _3procent) / 100.0; // 2 decimals

        String message = String.format("Deposit fiat: %.2f %s for %s, ", messageValue, depositFiatParams.currency, user.email);

        // Line item details
        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", depositFiatParams.currency);
        priceData.put("product_data", Map.of("name", message));
        priceData.put("unit_amount", depositFiatParams.amount);

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price_data", priceData);
        lineItem.put("quantity", 1);

        // Line items list
        List<Object> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        String successUrl = String.format("http://localhost:3000/vxpayment/sucess?paymentId=%s", "depositFiat");
        String cancelUrl = String.format("http://localhost:3000/vxpayment/cancel?paymentId=%s", "depositFiat");

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
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();
        return stripeSessionResponse;
    }

    private StripeSessionCreateResponse createStripeSessionDepositFiatV2(String stripeSecretKey,
                                                                       VxUser user,
                                                                       VxStripeConfig userStripeConfig,
                                                                       PaymentDepositFiatParams depositFiatParams) throws
            StripeException {

        Stripe.apiKey = stripeSecretKey;

        Double _3procent = (3.0 * Double.valueOf(depositFiatParams.amount)) / 100.0;
        double messageValue = (Double.valueOf(depositFiatParams.amount) - _3procent) / 100.0; // 2 decimals

        String message = String.format("Deposit fiat: %.2f %s for %s, ", messageValue, depositFiatParams.currency, user.email);

        PriceCreateParams priceParams = PriceCreateParams.builder()
                .setCurrency(depositFiatParams.currency)
                .setProductData(
                        PriceCreateParams.ProductData.builder()
                                .setName(message)
                                .build()
                )
                .setUnitAmount(depositFiatParams.amount)
                .build();

        Price price = Price.create(priceParams);

        // Line item details
        SessionCreateParams.LineItem.Builder lineItemBuilder = SessionCreateParams.LineItem.builder()
                .setPrice(price.getId()) // Use the ID of the dynamically created price
                .setQuantity(1L);

        String successUrl = String.format("http://localhost:3000/vxpayment/sucess?paymentId=%s", "depositFiat");
        String cancelUrl = String.format("http://localhost:3000/vxpayment/cancel?paymentId=%s", "depositFiat");

        // Session parameters
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(lineItemBuilder.build())
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl);

        SessionCreateParams.PaymentIntentData.Builder paymentIntentDataBuilder = SessionCreateParams.PaymentIntentData.builder();

        // You can add payment intent data as needed
         paymentIntentDataBuilder.setApplicationFeeAmount(0L)
                 .setTransferData(
                         SessionCreateParams.PaymentIntentData.TransferData.builder()
                                 .setDestination(userStripeConfig.stripeAccountId)
                                 .build()
                 );

        paramsBuilder.setPaymentIntentData(paymentIntentDataBuilder.build());

        SessionCreateParams params = paramsBuilder.build();

        Session session = Session.create(params);
        System.out.println("Checkout Session URL: " + session.getUrl());
        System.out.println("StripeSessionId = " + session.getId());
        System.out.println("paymentId");

        StripeSessionCreateResponse stripeSessionResponse = new StripeSessionCreateResponse();
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();
        return stripeSessionResponse;
    }



}
