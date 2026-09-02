package api;

import client.ProductClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SuppliersApiTest extends BaseApiTest {

    @Test
    void createSupplierShouldReturn201() {

        ProductClient productClient = new ProductClient();

        String accessToken = getAccessToken();

        String supplierRequest = """
        {
          "name": "Test Supplier %s",
          "contact_name": "Test Contact",
          "contact_email": "supplier@example.com",
          "phone_number": "+37360000000",
          "address": "Test Address",
          "country": "Moldova",
          "city": "Chisinau",
          "website": "https://example.com/"
        }
        """.formatted(System.currentTimeMillis());

        Response response =
                productClient.createSupplier(
                        accessToken,
                        supplierRequest
                );

        assertEquals(201, response.statusCode());

        String supplierId =
                response.jsonPath().getString("supplier_id");

        assertNotNull(supplierId);
    }

    @Test
    void createSupplierShouldReturn422ForDuplicateName() {

        ProductClient productClient = new ProductClient();

        String accessToken = getAccessToken();

        String supplierName = "Duplicate Supplier " + System.currentTimeMillis();

        String supplierRequest = """
    {
      "name": "%s",
      "contact_name": "Test Contact",
      "contact_email": "supplier@example.com",
      "phone_number": "+37360000000",
      "address": "Test Address",
      "country": "Moldova",
      "city": "Chisinau",
      "website": "https://example.com/"
    }
    """.formatted(supplierName);

        // First supplier
        Response firstResponse =
                productClient.createSupplier(
                        accessToken,
                        supplierRequest
                );

        assertEquals(201, firstResponse.statusCode());

        // Second supplier with the same name
        Response secondResponse =
                productClient.createSupplier(
                        accessToken,
                        supplierRequest
                );

        assertEquals(422, secondResponse.statusCode());
    }
}