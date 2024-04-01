package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.PublicEventClientPaymentReportResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventCheckRegisterClientResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventClientDepositFundsResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
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

        // Make the HTTP request
        ResponseEntity<PublicEventClientPaymentReportResponse> responseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/publicEventClientPayment/getClientReport/event/" + publicEventId + "/client/" + clientId,
                PublicEventClientPaymentReportResponse.class,
                headers);

        // Check the response status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Return the response body
        return responseEntity.getBody();
    }
}
