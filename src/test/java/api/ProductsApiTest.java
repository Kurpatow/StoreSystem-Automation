package api;

import client.AuthClient;
import client.ProductClient;
import io.restassured.response.Response;
import model.ProductRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductsApiTest {

    @Test
    void getProductByIdShouldReturn200ForAuthorizedUser() {

        String email = "test" + System.currentTimeMillis() + "@example.com";
        String password = "Test123456";
        String name = "Test User";

        AuthClient authClient = new AuthClient();
        ProductClient productClient = new ProductClient();

        // 1. Register
        authClient.register(name, email, password);

        // 2. Login
        String accessToken = authClient.login(email, password);

        // 3. Create supplier
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

        Response supplierResponse =
                productClient.createSupplier(accessToken, supplierRequest);

        System.out.println("SUPPLIER RESPONSE:");
        System.out.println(supplierResponse.asPrettyString());

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        System.out.println("SUPPLIER ID: " + supplierId);

        assertEquals(false, supplierId == null);

        // 4. Create product
        String productRequest = """
{
  "name": "Test Product %s",
  "description": "Product created by automated test",
  "category": "TestCategory",
  "price": 100,
  "stock_quantity": 10,
  "supplier_id": "%s",
  "image_url": "https://example.com/product.jpg",
  "weight": 1.5,
  "dimensions": "10x10x10",
  "manufacturer": "TestManufacturer"
}
""".formatted(System.currentTimeMillis(), supplierId);

        Response productResponse =
                productClient.createProduct(accessToken, productRequest);

        System.out.println("PRODUCT RESPONSE:");
        System.out.println(productResponse.asPrettyString());

        assertEquals(201, productResponse.statusCode());

        String productId =
                productResponse.jsonPath().getString("product_id");

        System.out.println("PRODUCT ID: " + productId);

        assertEquals(false, productId == null);

        // 5. Get product by ID
        Response getProductResponse =
                productClient.getProductById(accessToken, productId);

        System.out.println("GET PRODUCT RESPONSE:");
        System.out.println(getProductResponse.asPrettyString());

        assertEquals(200, getProductResponse.statusCode());

        assertEquals(
                productId,
                getProductResponse.jsonPath().getString("product_id")
        );

        // 6. Update product availability
        Response updateAvailabilityResponse =
                productClient.updateAvailability(accessToken, productId, true);

        System.out.println("UPDATE AVAILABILITY RESPONSE:");
        System.out.println(updateAvailabilityResponse.asPrettyString());

        assertEquals(200, updateAvailabilityResponse.statusCode());

        // 7. Get product again and verify availability
        Response updatedProductResponse =
                productClient.getProductById(accessToken, productId);

        System.out.println("UPDATED PRODUCT RESPONSE:");
        System.out.println(updatedProductResponse.asPrettyString());

        assertEquals(200, updatedProductResponse.statusCode());

        assertEquals(
                true,
                updatedProductResponse.jsonPath().getBoolean("is_available")
        );

        // 8. Update product
        String updatedProductName = "Updated Product " + System.currentTimeMillis();

        String updateProductRequest = """
{
  "name": "%s",
  "description": "Updated product description",
  "category": "UpdatedCategory",
  "price": 200,
  "stock_quantity": 25,
  "supplier_id": "%s",
  "image_url": "https://example.com/updated-product.jpg",
  "weight": 2.5,
  "dimensions": "20x20x20",
  "manufacturer": "UpdatedManufacturer"
}
""".formatted(updatedProductName, supplierId);

        Response updateProductResponse =
                productClient.updateProduct(
                        accessToken,
                        productId,
                        updateProductRequest
                );

        System.out.println("UPDATE PRODUCT RESPONSE:");
        System.out.println(updateProductResponse.asPrettyString());

        assertEquals(200, updateProductResponse.statusCode());

        // 9. Get updated product and verify changes
        Response getUpdatedProductResponse =
                productClient.getProductById(accessToken, productId);

        System.out.println("GET UPDATED PRODUCT RESPONSE:");
        System.out.println(getUpdatedProductResponse.asPrettyString());

        assertEquals(200, getUpdatedProductResponse.statusCode());

        assertEquals(
                updatedProductName,
                getUpdatedProductResponse.jsonPath().getString("name")
        );

        assertEquals(
                "Updated product description",
                getUpdatedProductResponse.jsonPath().getString("description")
        );

        assertEquals(
                25,
                getUpdatedProductResponse.jsonPath().getInt("stock_quantity")
        );

        assertEquals(
                "UpdatedCategory",
                getUpdatedProductResponse.jsonPath().getString("category")
        );
    }
}