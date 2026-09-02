package api;

import client.AuthClient;
import client.ProductClient;
import io.restassured.response.Response;
import model.ProductRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductsApiTest extends BaseApiTest {

    @Test
    void getProductByIdShouldReturn200ForAuthorizedUser() {

        ProductClient productClient = new ProductClient();

        String accessToken = getAccessToken();

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

    @Test
    void getProductsShouldReturn401WithoutAuthorization() {

        ProductClient productClient = new ProductClient();

        Response response = productClient.getAllProducts("");

        assertEquals(401, response.statusCode());
    }

    @Test
    void createProductShouldReturn401WithoutAuthorization() {

        ProductClient productClient = new ProductClient();

        String productRequest = """
    {
      "name": "Unauthorized Product %s",
      "description": "Product without authorization",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": 10,
      "supplier_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(System.currentTimeMillis());

        Response response =
                productClient.createProduct("", productRequest);

        assertEquals(401, response.statusCode());
    }

    @Test
    void createProductShouldReturn422ForDuplicateName() {

        ProductClient productClient = new ProductClient();

        String accessToken = getAccessToken();

        // Create supplier
        String supplierRequest = """
    {
      "name": "Duplicate Product Supplier %s",
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
                productClient.createSupplier(
                        accessToken,
                        supplierRequest
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        assertEquals(false, supplierId == null);

        // Product with unique name
        String productName =
                "Duplicate Product " + System.currentTimeMillis();

        String productRequest = """
    {
      "name": "%s",
      "description": "Product for duplicate test",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(productName, supplierId);

        // First product
        Response firstResponse =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(201, firstResponse.statusCode());

        // Second product with the same name
        Response secondResponse =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(422, secondResponse.statusCode());
    }

    @Test
    void createProductShouldReturn422ForDuplicateNameIgnoringCase() {

        ProductClient productClient = new ProductClient();

        String accessToken = getAccessToken();

        // Create supplier
        String supplierRequest = """
    {
      "name": "Case Test Supplier %s",
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
                productClient.createSupplier(
                        accessToken,
                        supplierRequest
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        assertEquals(false, supplierId == null);

        String productName =
                "Case Test Product " + System.currentTimeMillis();

        // First product
        String firstProductRequest = """
    {
      "name": "%s",
      "description": "First product",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(productName, supplierId);

        Response firstResponse =
                productClient.createProduct(
                        accessToken,
                        firstProductRequest
                );

        assertEquals(201, firstResponse.statusCode());

        // Second product with the same name but different case
        String secondProductRequest = """
    {
      "name": "%s",
      "description": "Second product",
      "category": "TestCategory",
      "price": 200,
      "stock_quantity": 20,
      "supplier_id": "%s",
      "image_url": "https://example.com/product2.jpg",
      "weight": 2.5,
      "dimensions": "20x20x20",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(productName.toLowerCase(), supplierId);

        Response secondResponse =
                productClient.createProduct(
                        accessToken,
                        secondProductRequest
                );

        assertEquals(422, secondResponse.statusCode());
    }
}