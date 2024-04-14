package eu.vxbank.api.publicevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import eu.vxbank.api.utils.components.SystemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicEventOrderItemIntegrationTest {

    private class Setup {
        Long userId;
        String vxToken;
        String stripeAccountId;
        String email;
        Long publicEventId;
        Long vxPublicEventClientId;
        List<VxPublicEventProduct> productList;

    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    @Test
    public void createTest() throws StripeException, FirebaseAuthException, JsonProcessingException {
        System.out.println("End of test");
    }

}
