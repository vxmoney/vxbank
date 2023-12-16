package eu.vxbank.api.stripe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.UserHelper;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicSendFundsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${stripeKey.devSecretKey}")
    private String stripeDevSecretKey;

    @Autowired
    SystemService systemService;

    private LoginResponse generateConfiguredSpecificLoggedInUser(String stripeAccountId) throws
            FirebaseAuthException,
            JsonProcessingException {
        VxBankDatastore ds = systemService.getVxBankDatastore();
        String coreMail = UUID.randomUUID()
                .toString();
        String email = String.format("$%s@mail.com", coreMail);

        // vxUser
        VxUser vxUser = VxUser.builder()
                .email(email)
                .build();
        VxDsService.persist(vxUser, ds, VxUser.class);
        Assertions.assertNotNull(vxUser.id);

        String vxToken = UserHelper.generateVxToken(vxUser.email, restTemplate, port);
        Assertions.assertNotNull(vxToken);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.id = vxUser.id;
        loginResponse.email = vxUser.email;
        loginResponse.vxToken = vxToken;


        // vxStripeConfig
        VxStripeConfig vxStripeConfig = VxStripeConfig.builder()
                .userId(vxUser.id)
                .stripeAccountId(stripeAccountId)
                .state(VxStripeConfig.State.active)
                .build();
        VxDsService.persist(vxStripeConfig, ds, VxStripeConfig.class);
        Assertions.assertNotNull(vxStripeConfig.id);

        return loginResponse;
    }

    @Test
    public void test00SendFunds() throws FirebaseAuthException, JsonProcessingException, StripeException {
        String stripeAccountId = "acct_1OLVFTBDXgpnX6Hr";

        Long amount = 1000L; // in web2 we use 2 decimals (this = 10)
        Transfer transfer = VxStripeUtil.sendFundsToStripeAccount(stripeDevSecretKey, stripeAccountId, amount, "eur");
        System.out.println("Hello test");
    }

}
