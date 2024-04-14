package eu.vxbank.api.helpers;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import vxbank.datastore.data.publicevent.VxPublicEventOrderItem;

public class PublicEventOrderItemHelper {

    public static VxPublicEventOrderItem get(TestRestTemplate restTemplate,
                                             int port,
                                             String vxToken,
                                             Long itemId,
                                             int expectedStatusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<VxPublicEventOrderItem> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventOrderItem/" + itemId,
                HttpMethod.GET,
                requestEntity,
                VxPublicEventOrderItem.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        VxPublicEventOrderItem response = responseEntity.getBody();
        return response;
    }
}
