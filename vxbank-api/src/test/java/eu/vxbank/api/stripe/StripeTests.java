package eu.vxbank.api.stripe;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vxbank.api.controlers.response.VxStripeResponse;
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
        VxStripeResponse stripeResponse = objectMapper.readValue(firstResponse, VxStripeResponse.class);

        Assertions.assertEquals("Public key message", stripeResponse.publicKeyMessage);
        Assertions.assertEquals("Secret key message", stripeResponse.secretKeyMessage);

        System.out.println("End of test");
    }

}
