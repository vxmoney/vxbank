package eu.vxbank.api.ping;

import eu.vxbank.api.endpoints.ping.dto.PingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PingIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetEnvironment(){
        String path = String.format("http://localhost:%d/ping/getEnvironment",port);
        PingResponse pingResponse =
                this.restTemplate.getForObject(path, PingResponse.class);


        System.out.println("Port = " + port);
    }
}
