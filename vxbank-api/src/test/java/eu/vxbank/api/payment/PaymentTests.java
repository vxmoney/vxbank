package eu.vxbank.api.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vxbank.api.endpoints.payment.dto.CreatePaymentIntentParams;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void createPaymentIntentTest()throws Exception{

        CreatePaymentIntentParams createParams = new CreatePaymentIntentParams();
        createParams.productId="id_01";
        createParams.productTitle= "Test title";
        createParams.productDescription = "Test description";

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(createParams);

        String stringResponse = mockMvc.perform(MockMvcRequestBuilders.post("/payments/create-payment-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        StripeSessionResponse stripeResponse = objectMapper.readValue(stringResponse, StripeSessionResponse.class);
        Assertions.assertNotNull(stripeResponse);
    }

}
