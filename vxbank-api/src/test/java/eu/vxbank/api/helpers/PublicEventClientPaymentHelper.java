package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentParams;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentResponse;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.PublicEventClientPaymentReportResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCheckRegisterClientResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventClientDepositFundsParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventClientDepositFundsResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PublicEventClientPaymentHelper {


    public static PublicEventClientPaymentReportResponse clientPaymentReport(TestRestTemplate restTemplate,
                                                                             int port,
                                                                             String vxToken,
                                                                             Long publicEventId,
                                                                             Long clientId,
                                                                             int expectedStatusCode) {
        // Setup the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);


        // Make the HTTP request
        String url = "http://localhost:" + port + "/publicEventClientPayment/getClientReport/event/" + publicEventId + "/client/" + clientId;
        ResponseEntity<PublicEventClientPaymentReportResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                PublicEventClientPaymentReportResponse.class);

        // Check the response status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Return the response body
        return responseEntity.getBody();
    }

    public static ManagerRegistersPaymentResponse managerRegistersPayment(TestRestTemplate restTemplate,
                                                                          int port,
                                                                          String vxToken,
                                                                          ManagerRegistersPaymentParams params,
                                                                          int expectedStatusCode) {

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the request body and headers
        HttpEntity<ManagerRegistersPaymentParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<ManagerRegistersPaymentResponse> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventClientPayment/managerRegistersPayment",
                HttpMethod.POST, requestEntity, ManagerRegistersPaymentResponse.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        ManagerRegistersPaymentResponse response = responseEntity.getBody();
        return response;
    }
}
