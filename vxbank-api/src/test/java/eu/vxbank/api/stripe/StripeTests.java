package eu.vxbank.api.stripe;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vxbank.api.controlers.models.createpaymentintent.CreatePaymentIntentParams;
import eu.vxbank.api.controlers.models.createpaymentintent.CreatePaymentIntentResponse;
import eu.vxbank.api.controlers.response.PingResponse;
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
public class StripeTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() throws Exception {
        // First, perform a request to /testStripe
        String firstResponse = mockMvc.perform(MockMvcRequestBuilders.get("/testStripe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        PingResponse stripeResponse = objectMapper.readValue(firstResponse, PingResponse.class);

        Assertions.assertEquals("test", stripeResponse.systemEnvironment);

        System.out.println("End of test");
    }

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

        CreatePaymentIntentResponse stripeResponse = objectMapper.readValue(stringResponse, CreatePaymentIntentResponse.class);
        Assertions.assertNotNull(stripeResponse);
    }

}
