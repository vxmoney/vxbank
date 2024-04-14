package eu.vxbank.api.publicevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import com.stripe.net.Webhook;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentParams;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentResponse;
import eu.vxbank.api.endpoints.publicevent.orderitem.dto.OrderItemParams;
import eu.vxbank.api.endpoints.publicevent.orderitem.dto.OrderItemSearchResponse;
import eu.vxbank.api.endpoints.publicevent.product.dto.ProductCreateParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.*;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointParams;
import eu.vxbank.api.endpoints.publicevent.sellingpoint.dto.SellingPointResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.*;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEventOrderItem;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;
import vxbank.datastore.data.service.VxDsService;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicEventOrderItemIntegrationTest {

    private class Setup {
        Long userId;
        String vxToken;
        String stripeAccountId;
        String email;
        Long publicEventId;
        Long vxPublicEventClientId;
        List<VxPublicEventProduct> productList;

        List<SellingPointResponse> sellingPointList;

    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    @Test
    public void createTest() throws StripeException, FirebaseAuthException, IOException, NoSuchAlgorithmException, InvalidKeyException {

        Setup owner = setupOwner("acct_1P05koBBqbt0qcrd");
        Setup manager = setupManager(owner.vxToken, owner.publicEventId);
        Setup client = setupClient(manager.publicEventId);

        Long value = 10000L;
        depositFunds(client.vxToken, client.publicEventId, client.vxPublicEventClientId, value);

        // build order item param list
        List<OrderItemParams> orderItemParamsList = new ArrayList<>();
        for (VxPublicEventProduct product : owner.productList) {
            OrderItemParams orderItemParams = OrderItemParams.builder()
                    .vxPublicEventProductId(product.getId())
                    .quantity(2L)
                    .value(2 * product.getPrice())
                    .build();
            orderItemParamsList.add(orderItemParams);
        }
        Long orderValue = orderItemParamsList.stream().mapToLong(OrderItemParams::getValue).sum();


        ManagerRegistersPaymentResponse response = PublicEventClientPaymentHelper.managerRegistersPayment(
                restTemplate,
                port,
                manager.vxToken,
                ManagerRegistersPaymentParams.builder()
                        .eventId(client.publicEventId)
                        .clientId(client.vxPublicEventClientId)
                        .vxPublicEventSellingPointId(owner.sellingPointList.get(0).getId())
                        .value(orderValue)
                        .orderItemParamsList(orderItemParamsList)
                        .build(),
                200);
        Assertions.assertEquals(6000L, response.updatedAvailableBalance);
        Assertions.assertEquals(orderItemParamsList.size(), response.publicEventOrderItemList.size());

        VxPublicEventOrderItem orderItem = response.publicEventOrderItemList.get(0);
        VxPublicEventOrderItem getItem = PublicEventOrderItemHelper.get(restTemplate, port, manager.vxToken, orderItem.getId(), 200);
        Assertions.assertEquals(orderItem.getId(), getItem.getId());
        Assertions.assertEquals(orderItem.getValue(), getItem.getValue());

        // getByLongIndexField
        OrderItemSearchResponse searchResponse = PublicEventOrderItemHelper.getByLongIndexField(restTemplate,
                port,
                manager.vxToken,
                VxPublicEventOrderItem.IndexedField.vxPublicEventSellingPointId,
                owner.sellingPointList.get(0).getId(),
                200);
        Assertions.assertEquals(2, searchResponse.orderItemList.size());

    }

    public Setup setupOwner(String stripeAccountId) throws
            StripeException,
            FirebaseAuthException,
            JsonProcessingException {
        Setup setup = setupUser(stripeAccountId);
        Long timeStamp = new Date().getTime();
        String title = "Event - " + timeStamp;
        PublicEventCreateParams params = PublicEventCreateParams.builder()
                .vxUserId(setup.userId)
                .vxIntegrationId(VxIntegrationId.vxEvents)
                .title(title)
                .currency("eur")
                .build();

        PublicEventCreateResponse publicEventCreateResponse = PublicEventHelper.create(restTemplate,
                port,
                setup.vxToken,
                params,
                200);
        setup.publicEventId = publicEventCreateResponse.id;

        // create products
        setup.productList = new ArrayList<>();
        VxPublicEventProduct productA = createProduct(setup.vxToken,
                setup.publicEventId,
                "ProductA - " + new Date().getTime(),
                1000L);
        VxPublicEventProduct productB = createProduct(setup.vxToken,
                setup.publicEventId,
                "ProductB - " + new Date().getTime(),
                1000L);
        setup.productList.add(productA);
        setup.productList.add(productB);

        // create selling points
        setup.sellingPointList = new ArrayList<>();
        SellingPointResponse sellingPointA = createSellingPoint(setup.vxToken,
                setup.publicEventId,
                "SellingPointA",
                List.of(productA.getId(), productB.getId()));
        setup.sellingPointList.add(sellingPointA);

        SellingPointResponse sellingPointB = createSellingPoint(setup.vxToken,
                setup.publicEventId,
                "SellingPointB",
                List.of(productA.getId(), productB.getId()));
        setup.sellingPointList.add(sellingPointB);

        return setup;
    }

    private SellingPointResponse createSellingPoint(String vxToken, Long publicEventId, String title, List<Long> productIdList) {
        SellingPointParams params = SellingPointParams.builder()
                .vxPublicEventId(publicEventId)
                .title(title)
                .productIdList(productIdList)
                .build();
        SellingPointResponse sellingPointResponse = PublicEventSellingPointHelper.create(restTemplate,
                port,
                vxToken,
                params,
                200);
        return sellingPointResponse;
    }

    private VxPublicEventProduct createProduct(String vxToken, Long publicEventId, String title, long price) {

        ProductCreateParams params = ProductCreateParams.builder()
                .vxPublicEventId(publicEventId)
                .title(title)
                .availability(VxPublicEventProduct.Availability.available)
                .price(price)
                .build();
        return PublicEventProductHelper.create(restTemplate, port, vxToken, params, 200);
    }

    private Setup setupUser(String stripeAccountId) throws
            FirebaseAuthException,
            JsonProcessingException,
            StripeException {


        Setup setup = new Setup();

        setup.email = RandomUtil.generateRandomEmail();
        setup.vxToken = UserHelper.generateVxToken(setup.email, restTemplate, port);
        setup.stripeAccountId = stripeAccountId;


        LoginResponse loginResponse = PingHelper.whoAmI(setup.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(setup.email, loginResponse.email);


        setup.userId = loginResponse.id;

        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = setup.userId;
        StripeConfigInitiateConfigResponse initiateConfigResponse = StripeConfigHelper.initiateConfig(setup.vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        VxBankDatastore ds = systemService.getVxBankDatastore();
        SideStripeConfigHelper.setStripeAccountId(ds, setup.userId, setup.stripeAccountId);

        Optional<VxUser> vxUser = VxDsService.getUserByEmail(setup.email, ds);
        Assertions.assertTrue(vxUser.isPresent());

        return setup;
    }

    private Setup setupManager(String ownerVxToken, Long publicEventId) throws
            FirebaseAuthException,
            JsonProcessingException {

        Setup manager = new Setup();

        manager.email = RandomUtil.generateRandomEmail();
        manager.vxToken = UserHelper.generateVxToken(manager.email, restTemplate, port);
        manager.publicEventId = publicEventId;


        LoginResponse loginResponse = PingHelper.whoAmI(manager.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(manager.email, loginResponse.email);


        manager.userId = loginResponse.id;


        VxBankDatastore ds = systemService.getVxBankDatastore();

        Optional<VxUser> vxUser = VxDsService.getUserByEmail(manager.email, ds);
        Assertions.assertTrue(vxUser.isPresent());

        //------------------
        Optional<VxUser> optionalUserB = VxDsService.getUserByEmail(manager.email, systemService.getVxBankDatastore());
        Assertions.assertTrue(optionalUserB.isPresent());

        // add manager
        PublicEventAddMangerParams goodParams = PublicEventAddMangerParams.builder()
                .publicEventId(publicEventId)
                .email(manager.email)
                .build();
        PublicEventHelper.addManager(restTemplate, port, ownerVxToken, goodParams, 200);
        return manager;
    }

    private Setup setupClient(Long publicEventId) throws
            StripeException,
            FirebaseAuthException,
            JsonProcessingException {

        // ---- setup part
        Setup client = new Setup();

        client.email = RandomUtil.generateRandomEmail();
        client.vxToken = UserHelper.generateVxToken(client.email, restTemplate, port);


        LoginResponse loginResponse = PingHelper.whoAmI(client.vxToken, restTemplate, port, 200);
        Assertions.assertEquals(client.email, loginResponse.email);


        client.userId = loginResponse.id;


        VxBankDatastore ds = systemService.getVxBankDatastore();

        Optional<VxUser> vxUser = VxDsService.getUserByEmail(client.email, ds);

        // ------- join event
        PublicEventCheckRegisterClientResponse checkRegisterClientResponse = PublicEventHelper.checkRegisterClient(
                restTemplate,
                port,
                client.vxToken,
                publicEventId,
                200);
        client.vxPublicEventClientId = checkRegisterClientResponse.id;
        client.publicEventId = publicEventId;
        return client;
    }

    void depositFunds(String vxToken, Long publicEventId, Long vxPublicEventClientId, Long value) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        // client deposit funds

        PublicEventClientDepositFundsParams depositFundsParams = PublicEventClientDepositFundsParams.builder()
                .value(value)
                .build();
        PublicEventClientDepositFundsResponse depositFundsResponse = PublicEventHelper.clientDepositFunds(restTemplate,
                port,
                vxToken,
                publicEventId,
                depositFundsParams,
                200);
        Assertions.assertNotNull(depositFundsResponse);
        Assertions.assertEquals(vxPublicEventClientId, depositFundsResponse.vxPublicEventClientId);
        Assertions.assertEquals(publicEventId, depositFundsResponse.vxPublicEventId);
        Assertions.assertNotNull(depositFundsResponse.vxEventPaymentId);
        Assertions.assertNotNull(depositFundsResponse.stripeSessionPaymentUrl);
        Assertions.assertNotNull(depositFundsResponse.stripeSessionId);

        // simulate stripeWebhook
        String fileName = "publicEventIntegrationTest/clientDepositFunds-00.json";
        String fileContent = loadFileAsString(fileName);
        String webhookSigningSecret = "whsec_b36f59fd7556a24cbdd59589110a616aebb7a35167d04d2aade484c8a345af53";
        String body = fileContent.replace("#tagStripeSessionId", depositFundsResponse.stripeSessionId);

        long timeStamp = (new Date()).getTime();
        String payload = timeStamp + "." + body;
        String signedPayload = Webhook.Util.computeHmacSha256(webhookSigningSecret, payload);
        String stripeSignature = "t=" + timeStamp + ",v1=" + signedPayload;

        WebhookHelper.handleStripeWebhook(restTemplate, port, stripeSignature, body, 200);


    }

    public String loadFileAsString(String fileName) throws IOException {
        // Get the class loader
        ClassLoader classLoader = getClass().getClassLoader();

        // Use the class loader to load the file as a resource
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        } else {
            try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
                // Use Scanner to read the content of the file into a string
                return scanner.useDelimiter("\\A")
                        .next();
            }
        }
    }

}
