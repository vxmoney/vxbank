package eu.vxbank.api.payment;

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
public class TestWebhook {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testStripeWebhook() throws Exception {

        String requestBody = "no-payload";

        String stringResponse = mockMvc.perform(MockMvcRequestBuilders.post("/stripeWebhook")
                        .header("Stripe-Signature", "your_valid_stripe_signature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(stringResponse);

    }
}
