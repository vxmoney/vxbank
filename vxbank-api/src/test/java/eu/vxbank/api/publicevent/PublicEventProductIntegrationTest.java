package eu.vxbank.api.publicevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.product.dto.ProductCreateParams;
import eu.vxbank.api.endpoints.publicevent.product.dto.ProductSearchResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.*;
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
import vxbank.datastore.data.publicevent.VxPublicEventProduct;
import vxbank.datastore.data.service.VxDsService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicEventProductIntegrationTest {
    private class Setup {
        Long userId;
        String vxToken;
        String stripeAccountId;
        String email;
        Long publicEventId;
        Long vxPublicEventClientId;

    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

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

    public Setup setupOwner(String stripeAccountId) throws StripeException, FirebaseAuthException, JsonProcessingException {
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
        return setup;
    }

    private Setup setupManager(String ownerVxToken, Long publicEventId) throws FirebaseAuthException, JsonProcessingException {

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
        PublicEventHelper.addManager(restTemplate,
                port,
                ownerVxToken,
                goodParams,
                200);
        return manager;
    }

    private Setup setupClient(Long publicEventId) throws StripeException, FirebaseAuthException, JsonProcessingException {
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
        PublicEventCheckRegisterClientResponse checkRegisterClientResponse = PublicEventHelper.checkRegisterClient(restTemplate,
                port,
                client.vxToken,
                publicEventId,
                200);
        client.vxPublicEventClientId = checkRegisterClientResponse.id;
        client.publicEventId = publicEventId;
        return client;
    }

    @Test
    public void createTest() throws StripeException, FirebaseAuthException, JsonProcessingException {


        Setup owner = setupOwner("acct_1P05koBBqbt0qcrd");
        Setup manager = setupManager(owner.vxToken, owner.publicEventId);
        Setup client = setupClient(manager.publicEventId);

        Assertions.assertEquals(owner.publicEventId, manager.publicEventId);
        Assertions.assertEquals(owner.publicEventId, client.publicEventId);
        Assertions.assertNotNull(client.vxPublicEventClientId);

        ProductCreateParams params = ProductCreateParams.builder()
                .vxPublicEventId(owner.publicEventId)
                .title("title-1")
                .description("description")
                .availability(VxPublicEventProduct.Availability.available)
                .price(250L)
                .build();
        VxPublicEventProduct product = PublicEventProductHelper.create(restTemplate,
                port,
                owner.vxToken,
                params,
                200);
        Assertions.assertNotNull(product.id);

        System.out.println("End of test");
    }

    @Test
    public void getTest() throws StripeException, FirebaseAuthException, JsonProcessingException {


        Setup owner = setupOwner("acct_1P05koBBqbt0qcrd");

        ProductCreateParams params = ProductCreateParams.builder()
                .vxPublicEventId(owner.publicEventId)
                .title("title-1")
                .description("description")
                .availability(VxPublicEventProduct.Availability.available)
                .price(250L)
                .build();
        VxPublicEventProduct product = PublicEventProductHelper.create(restTemplate,
                port,
                owner.vxToken,
                params,
                200);

        VxPublicEventProduct getProduct = PublicEventProductHelper.get(restTemplate,
                port,
                owner.vxToken,
                product.id,
                200);
        Assertions.assertEquals(product.id, getProduct.id);
        Assertions.assertEquals(product.title, getProduct.title);
        Assertions.assertEquals(product.price, getProduct.price);

    }

    @Test
    public void updateTest() throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup owner = setupOwner("acct_1P05koBBqbt0qcrd");
        ProductCreateParams params = ProductCreateParams.builder()
                .vxPublicEventId(owner.publicEventId)
                .title("title-1")
                .description("description")
                .availability(VxPublicEventProduct.Availability.available)
                .price(250L)
                .build();
        VxPublicEventProduct product = PublicEventProductHelper.create(restTemplate,
                port,
                owner.vxToken,
                params,
                200);

        ProductCreateParams updateParams = ProductCreateParams.builder()
                .title("title-2")
                .description("description-2")
                .availability(VxPublicEventProduct.Availability.notAvailable)
                .price(300L)
                .build();
        VxPublicEventProduct updatedProduct = PublicEventProductHelper.update(restTemplate,
                port,
                owner.vxToken,
                product.id,
                updateParams,
                200);

        VxPublicEventProduct getProduct = PublicEventProductHelper.get(restTemplate,
                port,
                owner.vxToken,
                product.id,
                200);
        Assertions.assertEquals(updatedProduct.id, getProduct.id);
        Assertions.assertEquals(updatedProduct.title, getProduct.title);
        Assertions.assertEquals(updatedProduct.price, getProduct.price);

        // check also against the update params
        Assertions.assertEquals(updatedProduct.title, updateParams.title);
        Assertions.assertEquals(updatedProduct.price, updateParams.price);

    }

    // test search by event id
    @Test
    public void searchByEventIdTest() throws StripeException, FirebaseAuthException, JsonProcessingException {
        Setup owner = setupOwner("acct_1P05koBBqbt0qcrd");
        ProductCreateParams params = ProductCreateParams.builder()
                .vxPublicEventId(owner.publicEventId)
                .title("title-1")
                .description("description")
                .availability(VxPublicEventProduct.Availability.available)
                .price(250L)
                .build();
        VxPublicEventProduct product = PublicEventProductHelper.create(restTemplate,
                port,
                owner.vxToken,
                params,
                200);

        ProductSearchResponse searchResponse = PublicEventProductHelper.search(restTemplate,
                port,
                owner.vxToken,
                owner.publicEventId,
                200);
        List<VxPublicEventProduct> products = searchResponse.productList;
        Assertions.assertEquals(1, products.size());
        VxPublicEventProduct item = products.get(0);

        Assertions.assertEquals(product.id, item.id);
        Assertions.assertEquals(product.title, item.title);
        Assertions.assertEquals(product.price, item.price);

    }




}
