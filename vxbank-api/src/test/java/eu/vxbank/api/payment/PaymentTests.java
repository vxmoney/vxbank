package eu.vxbank.api.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vxbank.api.endpoints.payment.dto.DeprecatedCreatePaymentIntentParams;
import eu.vxbank.api.endpoints.payment.dto.DeprecatedStripeSessionResponse;
import eu.vxbank.api.endpoints.payment.dto.PaymentCreateParams;
import eu.vxbank.api.testutils.BuildUtils;
import eu.vxbank.api.testutils.SetupUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxModel;
import vxbank.datastore.data.models.VxPayment;
import vxbank.datastore.data.models.VxServiceIntegration;
import vxbank.datastore.data.models.VxUser;

import java.util.Date;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void deprecatedPaymentTest() throws Exception {

        DeprecatedCreatePaymentIntentParams createParams = new DeprecatedCreatePaymentIntentParams();
        createParams.productId = "id_01";
        createParams.productTitle = "Test title";
        createParams.productDescription = "Test description";

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(createParams);

        String stringResponse = mockMvc.perform(MockMvcRequestBuilders.post("/payments/create-payment-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        DeprecatedStripeSessionResponse stripeResponse = objectMapper.readValue(stringResponse, DeprecatedStripeSessionResponse.class);
        Assertions.assertNotNull(stripeResponse);
    }

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

        VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());

        SetupUtils.createVxUser(vxUser, ds);

        String serviceTitle = generateString();
        VxServiceIntegration vxServiceIntegration = VxServiceIntegration.builder()
                .userId(vxUser.id)
                .title(serviceTitle)
                .build();
        SetupUtils.persistVxModel(vxServiceIntegration, ds);

        Assertions.assertNotNull(vxServiceIntegration.id);

        Long timeStamp = new Date().getTime();
        VxPayment vxPayment = VxPayment.builder()
                .vxUserId(vxUser.id)
                .vxServiceIntegrationId(vxServiceIntegration.id)
                .state(VxPayment.State.pending)
                .createTimeStamp(timeStamp)
                .currency("eur")
                .productName("Random product name")
                .valuePayedByUser(1000L) // 10 Euro
                .build();
        SetupUtils.persistVxModel(vxPayment, ds);
        Assertions.assertNotNull(vxPayment.id);

        PaymentCreateParams createParams = PaymentCreateParams.builder()
                .vxUserId(vxUser.id)
                .vxServiceIntegrationId(vxServiceIntegration.id)
                .vxPaymentId(vxPayment.id)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(createParams);


        String stringResponse = mockMvc.perform(MockMvcRequestBuilders.post("/payments/create-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

    }

}
