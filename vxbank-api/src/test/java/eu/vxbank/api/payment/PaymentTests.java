package eu.vxbank.api.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vxbank.api.endpoints.payment.dto.DeprecatedCreatePaymentIntentParams;
import eu.vxbank.api.endpoints.payment.dto.DeprecatedStripeSessionResponse;
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
import vxbank.datastore.data.models.VxServiceIntegration;
import vxbank.datastore.data.models.VxUser;

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

    @Test
    void createPaymentTest() {

        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(10, useLetters, useNumbers);
        String mail = String.format("%s@mail.com", generatedString);

        VxUser vxUser = BuildUtils.buildVxUserEmailOnly(mail);

        VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());

        SetupUtils.createVxUser(vxUser, ds);

        String serviceTitle = RandomStringUtils.random(10, useLetters, useNumbers);
        VxServiceIntegration serviceIntegration = VxServiceIntegration.builder()
                .userId(vxUser.id)
                .title(serviceTitle)
                .build();
        SetupUtils.persistVxModel(serviceIntegration,ds);

        Assertions.assertNotNull(serviceIntegration.id);


    }

}
