package eu.vxbank.api.ping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuthException;
import eu.vxbank.api.endpoints.ping.dto.*;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigParams;
import eu.vxbank.api.endpoints.stripe.dto.StripeConfigInitiateConfigResponse;
import eu.vxbank.api.endpoints.user.dto.Funds;
import eu.vxbank.api.endpoints.user.dto.LoginResponse;
import eu.vxbank.api.helpers.PingHelper;
import eu.vxbank.api.helpers.RandomUtil;
import eu.vxbank.api.helpers.StripeConfigHelper;
import eu.vxbank.api.helpers.UserHelper;
import eu.vxbank.api.sidehelpers.SideStripeConfigHelper;
import eu.vxbank.api.utils.components.SystemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PingIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    SystemService systemService;

    @Test
    public void testGetEnvironment() {
        String path = String.format("http://localhost:%d/ping/getEnvironment", port);
        PingResponse pingResponse = this.restTemplate.getForObject(path, PingResponse.class);


        System.out.println("Port = " + port);

        Package springSecurityPackage = org.springframework.security.core.Authentication.class.getPackage();
        String springSecurityVersion = springSecurityPackage.getImplementationVersion();
        System.out.println("Spring security version: " + springSecurityVersion);
    }

    @Test
    public void generateFirebaseIdToken() {
        String path = String.format("http://localhost:%d/ping/generateFirebaseIdToken", port);

        PingResponse pingResponse = this.restTemplate.getForObject(path, PingResponse.class);
        Assertions.assertNotNull(pingResponse.testFirebaseIdToken);
    }

    private LoginResponse setupUser(String stripeId) throws FirebaseAuthException, JsonProcessingException {


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

        VxBankDatastore ds = systemService.getVxBankDatastore();
        SideStripeConfigHelper.setStripeAccountId(ds, vxUserId, stripeId);

        loginResponse = PingHelper.whoAmI(vxToken, restTemplate, port, 200);
        loginResponse.vxToken = vxToken;

        return loginResponse;

    }

    @Test
    public void testFaucetValues() throws FirebaseAuthException, JsonProcessingException {


        LoginResponse loginResponse = setupUser("acct_1OPQvwPmPYe3loud");
        Funds initialFunds = loginResponse.availableFundsList.stream()
                .filter(fItem -> fItem.getCurrency()
                        .equals("eur"))
                .findFirst()
                .get();

        PingRequestFundsParams params = PingRequestFundsParams.builder()
                .userId(loginResponse.id)
                .amount(1000L)
                .currency("eur")
                .build();

        PingRequestFundsResponse requestFundsResponse = PingHelper.requestFunds(restTemplate,
                port,
                loginResponse.vxToken,
                params,
                200);

        Assertions.assertNotNull(requestFundsResponse);

        Funds funds = requestFundsResponse.fundsList.stream()
                .filter(fItem -> fItem.getCurrency()
                        .equals("eur"))
                .findFirst()
                .get();
        Assertions.assertTrue(funds.amount > initialFunds.amount);


    }

    @Test
    public void initiateVxGamingEurCurrency() throws FirebaseAuthException, JsonProcessingException {

        String email = RandomUtil.generateRandomEmail();
        String vxToken = UserHelper.generateVxToken(email, restTemplate, port);

        PingInitiateVxGamingResponse initiateResponse = PingHelper.initiateVxGaming(vxToken, restTemplate, port, 200);

        Assertions.assertNotNull(initiateResponse);
        Assertions.assertNotNull(initiateResponse.payUrl);

        System.out.println("Urd: " + initiateResponse.payUrl);
        System.out.println("Use card: 4000000000000077");
    }

    @Test
    public void initiateVxGamingRonCurrency() throws FirebaseAuthException, JsonProcessingException {
        String email = RandomUtil.generateRandomEmail();
        String vxToken = UserHelper.generateVxToken(email, restTemplate, port);

        InitiateVxGamingParams initiateVxGamingParams = new InitiateVxGamingParams();
        initiateVxGamingParams.currency = "ron";

        PingInitiateVxGamingResponse initiateResponse = PingHelper.initiateVxGamingCurrency(vxToken,
                restTemplate,
                port,
                initiateVxGamingParams,
                200);

        Assertions.assertNotNull(initiateResponse);
        Assertions.assertNotNull(initiateResponse.payUrl);

        System.out.println("Urd: " + initiateResponse.payUrl);
        System.out.println("Use card: 4000000000000077");
    }

    @Test
    public void testFaucetRonTest() throws FirebaseAuthException, JsonProcessingException {


        // stripe id: acct_1OPQvwPmPYe3loud

        //LoginResponse loginResponse = setupUser("acct_1OPQvwPmPYe3loud"); // eur
        //LoginResponse loginResponse = setupUser("acct_1OgLAUBRcIPQP1ZF"); // ron
        LoginResponse loginResponse = setupUser("acct_1OgqHAB36QPiP0qI"); // eur + ron


        loginResponse = UserHelper.refreshToken(restTemplate,
                loginResponse.vxToken,
                port,
                200);

//        Funds initialFunds = loginResponse.availableFundsList.stream()
//                .filter(fItem -> fItem.getCurrency()
//                        .equals("ron"))
//                .findFirst()
//                .get();

        PingRequestFundsParams params = PingRequestFundsParams.builder()
                .userId(loginResponse.id)
                .amount(1000L)
                .currency("ron")
                .build();

        PingRequestFundsResponse requestFundsResponse = PingHelper.requestFunds(restTemplate,
                port,
                loginResponse.vxToken,
                params,
                200);

        Assertions.assertNotNull(requestFundsResponse);

        Funds funds = requestFundsResponse.fundsList.stream()
                .filter(fItem -> fItem.getCurrency()
                        .equals("ron"))
                .findFirst()
                .get();
        Assertions.assertTrue(funds.amount > 0);

    }

    @Test
    public void testFaucetEurTest() throws FirebaseAuthException, JsonProcessingException {


        // stripe id: acct_1OPQvwPmPYe3loud

        //LoginResponse loginResponse = setupUser("acct_1OPQvwPmPYe3loud"); // eur
        //LoginResponse loginResponse = setupUser("acct_1OgLAUBRcIPQP1ZF"); // ron
        LoginResponse loginResponse = setupUser("acct_1OPQvwPmPYe3loud"); // eur


        loginResponse = UserHelper.refreshToken(restTemplate,
                loginResponse.vxToken,
                port,
                200);

//        Funds initialFunds = loginResponse.availableFundsList.stream()
//                .filter(fItem -> fItem.getCurrency()
//                        .equals("ron"))
//                .findFirst()
//                .get();

        PingRequestFundsParams params = PingRequestFundsParams.builder()
                .userId(loginResponse.id)
                .amount(1000L)
                .currency("eur")
                .build();

        PingRequestFundsResponse requestFundsResponse = PingHelper.requestFunds(restTemplate,
                port,
                loginResponse.vxToken,
                params,
                200);

        Assertions.assertNotNull(requestFundsResponse);

        Funds funds = requestFundsResponse.fundsList.stream()
                .filter(fItem -> fItem.getCurrency()
                        .equals("eur"))
                .findFirst()
                .get();
        Assertions.assertTrue(funds.amount > 0);

    }

}
