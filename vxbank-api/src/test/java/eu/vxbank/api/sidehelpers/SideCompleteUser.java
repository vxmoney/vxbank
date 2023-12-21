package eu.vxbank.api.sidehelpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.helpers.RandomUtil;
import eu.vxbank.api.helpers.StripeConfigHelper;
import eu.vxbank.api.helpers.UserHelper;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;

public class SideCompleteUser {
    public static LoginResponse setupUser(String stripeId, TestRestTemplate restTemplate, int port, VxBankDatastore ds) throws FirebaseAuthException, JsonProcessingException {


        String email = RandomUtil.generateRandomEmail();
        String vxToken = UserHelper.generateVxToken(email, restTemplate, port);

        LoginResponse loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        Assertions.assertEquals(email, loginResponse.email);

        VxUser vxUser = new VxUser();
        vxUser.id = loginResponse.id;
        vxUser.email = email;
        StripeConfigInitiateConfigParams initiateConfigParams = new StripeConfigInitiateConfigParams();
        initiateConfigParams.userId = vxUser.id;
        StripeConfigInitiateConfigResponse initiateConfigResponse = StripeConfigHelper.initiateConfig(vxToken,
                initiateConfigParams,
                restTemplate,
                port,
                200);

        Long vxUserId = vxUser.id;

        SideStripeConfigHelper.setStripeAccountId(ds, vxUserId, stripeId);

        loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        loginResponse.vxToken = vxToken;
        loginResponse.stripeId = stripeId;

        return loginResponse;

    }
}
