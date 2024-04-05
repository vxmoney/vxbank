package eu.vxbank.api.helpers;

import eu.vxbank.api.endpoints.publicevent.product.dto.ProductCreateParams;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;

public class PublicEventProductHelper {
    public static VxPublicEventProduct create(TestRestTemplate restTemplate,
                                              int port,
                                              String vxToken,
                                              ProductCreateParams params,
                                              int expectedStatusCode) {
        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create the HTTP entity with the request body and headers
        HttpEntity<ProductCreateParams> requestEntity = new HttpEntity<>(params, headers);

        // Make the POST request
        ResponseEntity<VxPublicEventProduct> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventProduct", HttpMethod.POST, requestEntity, VxPublicEventProduct.class);

        // check status code
        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        // Extract the response
        VxPublicEventProduct response = responseEntity.getBody();
        return response;
    }

    public static VxPublicEventProduct get(TestRestTemplate restTemplate,
                                           int port,
                                           String vxToken,
                                           Long productId,
                                           int expectedStatusCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + vxToken);

        // Create the HTTP entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request to /ping/whoAmI
        ResponseEntity<VxPublicEventProduct> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/publicEventProduct/" + productId,
                HttpMethod.GET,
                requestEntity,
                VxPublicEventProduct.class);

        int statusCode = responseEntity.getStatusCodeValue();
        Assertions.assertEquals(expectedStatusCode, statusCode);

        VxPublicEventProduct response = responseEntity.getBody();
        return response;
    }


}
