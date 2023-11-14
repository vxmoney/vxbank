package eu.vxbank.api.ping;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vxbank.api.endpoints.ping.PingResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class PingEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getEnvironment() throws Exception {
        String rawResponse = mockMvc.perform(MockMvcRequestBuilders.get("/ping/getEnvironment"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        PingResponse pingResponse = objectMapper.readValue(rawResponse, PingResponse.class);
        Assertions.assertNotNull(pingResponse);


    }
}