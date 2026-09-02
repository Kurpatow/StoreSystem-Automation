package api;

import data.TestData;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SuppliersApiTest extends BaseApiTest {

    @Test
    void createSupplierShouldReturn201() {

        String accessToken = getAccessToken();

        String supplierName = "Test Supplier " + System.currentTimeMillis();

        String supplierRequest =
                TestData.supplierRequest(supplierName);

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

        String accessToken = getAccessToken();

        String supplierName = "Duplicate Supplier " + System.currentTimeMillis();

        String supplierRequest =
                TestData.supplierRequest(supplierName);

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

    @Test
    void createSupplierShouldReturn422ForDuplicateNameIgnoringCase() {

        String accessToken = getAccessToken();

        String supplierName =
                "Case Supplier " + System.currentTimeMillis();

        String supplierRequest =
                TestData.supplierRequest(supplierName);

        Response firstResponse =
                productClient.createSupplier(
                        accessToken,
                        supplierRequest
                );

        assertEquals(201, firstResponse.statusCode());

        String upperCaseRequest =
                TestData.supplierRequest(supplierName.toUpperCase());

        Response secondResponse =
                productClient.createSupplier(
                        accessToken,
                        upperCaseRequest
                );

        assertEquals(422, secondResponse.statusCode());
    }
}