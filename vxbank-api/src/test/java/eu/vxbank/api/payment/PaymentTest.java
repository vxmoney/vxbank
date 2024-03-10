package eu.vxbank.api.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import eu.vxbank.api.endpoints.payment.dto.PaymentCreateParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatParams;
import eu.vxbank.api.endpoints.payment.dto.PaymentDepositFiatResponse;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionCreateResponse;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.*;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.testutils.BuildUtils;
import eu.vxbank.api.testutils.SetupUtils;
import eu.vxbank.api.utils.components.SystemService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.models.VxUser;

import java.io.Console;
import java.util.Date;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PaymentTest {

    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    SystemService systemService;

    private String generateString() {
        boolean useLetters = true;
        boolean useNumbers = false;
        String randomString = RandomStringUtils.random(10, useLetters, useNumbers);
        return randomString;
    }

    private String generateMail() {
        String randomString = generateString();
        String mail = String.format("%s@mail.com", randomString);
        return mail;
    }

    @Test
    void createPaymentTest() throws Exception {
        String mail = generateMail();

        VxUser vxUser = BuildUtils.buildVxUserEmailOnly(mail);

        VxBankDatastore ds = VxBankDatastore.init("my-project",
                VxBankDatastore.ConnectionType.localhost,
                Optional.empty());

        SetupUtils.createVxUser(vxUser, ds);

        String serviceTitle = generateString();
        Long vxStripeConfigId = 1L;

        Long timeStamp = new Date().getTime();
        VxPayment vxPayment = VxPayment.builder()
                .vxUserId(vxUser.id)
                .vxStripeConfigId(vxStripeConfigId)
                .state(VxPayment.State.pending)
                .createTimeStamp(timeStamp)
                .currency("eur")
                .productName("Random product name")
                .valuePayedByUser(10000000L) // 10 Euro
                .build();
        SetupUtils.persistVxModel(vxPayment, ds);
        Assertions.assertNotNull(vxPayment.id);

        PaymentCreateParams createParams = PaymentCreateParams.builder()
                .vxUserId(vxUser.id)
                .vxStripeConfigId(vxStripeConfigId)
                .vxPaymentId(vxPayment.id)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(createParams);


        String stringResponse = mockMvc.perform(MockMvcRequestBuilders.post("/example/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        StripeSessionCreateResponse stripeResponse = objectMapper.readValue(stringResponse,
                StripeSessionCreateResponse.class);
        Assertions.assertNotNull(stripeResponse);
        Assertions.assertEquals(vxPayment.id, stripeResponse.vxPaymentId);
        System.out.println("stripeResponse.stripeSessionId= " + stripeResponse.stripeSessionId);
        System.out.println("Use 4000000000000077 test card");
        System.out.println("stripeResponse.url= ");
        System.out.println(stripeResponse.url);
    }

    // ----------------

    private LoginResponse setupUser(String stripeId) throws FirebaseAuthException, JsonProcessingException {


        String email = RandomUtil.generateRandomEmail();
        String vxToken = UserHelper.generateVxToken(email, restTemplate, port);

        LoginResponse loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        Assertions.assertEquals(email, loginResponse.email);

        VxUser vxUser = new VxUser();
        vxUser.id = loginResponse.id;
        vxUser.email = email;
        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = vxUser.id;
        StripeConfigInitiateConfigResponse initiateConfigResponse = StripeConfigHelper.initiateConfig(vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        Long vxUserId = vxUser.id;

        VxBankDatastore ds = systemService.getVxBankDatastore();
        SideStripeConfigHelper.setStripeAccountId(ds, vxUserId, stripeId);

        loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        loginResponse.vxToken = vxToken;

        return loginResponse;

    }

    /**
     * All I need to test here is that link gets created. The rest is
     * handeld by stripe
     */
    @Test
    void initiateDepositFiatTest() throws FirebaseAuthException, JsonProcessingException {
        LoginResponse loginResponse = setupUser("acct_1OgqHAB36QPiP0qI"); // eur + ron
        loginResponse = UserHelper.refreshToken(restTemplate,
                loginResponse.vxToken,
                port,
                200);

        long amount = 100L;
        String currency = "eur";
        PaymentDepositFiatParams params = PaymentDepositFiatParams.builder()
                .userId(loginResponse.id)
                .amount(amount)
                .currency(currency)
                .build();

        PaymentDepositFiatResponse depositFiatResponse = PaymentHelper.depositFiat(
                restTemplate,
                port,
                loginResponse.vxToken,
                params,
                200);

        Assertions.assertNotNull(depositFiatResponse);
        System.out.println("Use 4000000000000077 test card");
        System.out.println("depositFiatResponse.payUrl = " + depositFiatResponse.payUrl);
    }


}
