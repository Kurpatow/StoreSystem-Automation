package api;

import client.ProductClient;
import data.TestData;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductsApiTest extends BaseApiTest {

    @Test
    void getProductByIdShouldReturn200ForAuthorizedUser() {

        String accessToken = getAccessToken();

        // 3. Create supplier
        String supplierName =
                "Test Supplier " + System.currentTimeMillis();

        String supplierRequest =
                TestData.supplierRequest(supplierName);

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
        String productName =
                "Test Product " + System.currentTimeMillis();

        String productRequest =
                TestData.productRequest(
                        productName,
                        supplierId
                );

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

        String updateProductRequest =
                TestData.updatedProductRequest(
                        updatedProductName,
                        supplierId
                );

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

        Response response = productClient.getAllProducts("");

        assertEquals(401, response.statusCode());
    }

    @Test
    void createProductShouldReturn401WithoutAuthorization() {

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

        String accessToken = getAccessToken();

        // Create supplier
        String supplierName =
                "Duplicate Product Supplier " + System.currentTimeMillis();

        String supplierRequest =
                TestData.supplierRequest(supplierName);

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

        String accessToken = getAccessToken();

        // Create supplier
        String supplierName =
                "Case Test Supplier " + System.currentTimeMillis();

        String supplierRequest =
                TestData.supplierRequest(supplierName);

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

    @Test
    void createProductShouldReturn404ForNonExistentSupplier() {

        String accessToken = getAccessToken();

        String nonExistentSupplierId =
                "00000000-0000-0000-0000-000000000001";

        String productRequest = """
    {
      "name": "Nonexistent Supplier Product %s",
      "description": "Product with nonexistent supplier",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(
                System.currentTimeMillis(),
                nonExistentSupplierId
        );

        Response response =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(404, response.statusCode());

        assertEquals(
                "Supplier not found",
                response.jsonPath().getString("detail")
        );
    }

    @Test
    void createProductShouldReturn422ForInvalidSupplierId() {

        String accessToken = getAccessToken();

        String invalidSupplierId = "abc";

        String productRequest = """
    {
      "name": "Invalid Supplier ID Product %s",
      "description": "Product with invalid supplier ID",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(
                System.currentTimeMillis(),
                invalidSupplierId
        );

        Response response =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(422, response.statusCode());
    }

    @Test
    void getProductByIdShouldReturn400ForInvalidProductId() {

        String accessToken = getAccessToken();

        Response response =
                productClient.getProductById(
                        accessToken,
                        "abc"
                );

        assertEquals(400, response.statusCode());
    }

    @Test
    void getProductsShouldReturn200ForAuthorizedUser() {

        String accessToken = getAccessToken();

        Response response =
                productClient.getAllProducts(accessToken);

        assertEquals(200, response.statusCode());
    }

    @Test
    void getProductByIdShouldReturn404ForNonExistentProduct() {

        String accessToken = getAccessToken();

        String nonExistentProductId =
                "00000000-0000-0000-0000-000000000001";

        Response response =
                productClient.getProductById(
                        accessToken,
                        nonExistentProductId
                );

        assertEquals(404, response.statusCode());

        assertEquals(
                "Product not found",
                response.jsonPath().getString("detail")
        );
    }

    @Test
    void deleteProductShouldReturn200() {

        String accessToken = getAccessToken();

        String supplierName =
                "Delete Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String productName =
                "Delete Product " + System.currentTimeMillis();

        Response productResponse =
                productClient.createProduct(
                        accessToken,
                        TestData.productRequest(productName, supplierId)
                );

        assertEquals(201, productResponse.statusCode());

        String productId =
                productResponse.jsonPath().getString("product_id");

        Response deleteResponse =
                productClient.deleteProduct(
                        accessToken,
                        productId
                );

        assertEquals(200, deleteResponse.statusCode());

        Response getResponse =
                productClient.getProductById(
                        accessToken,
                        productId
                );

        assertEquals(404, getResponse.statusCode());

        assertEquals(
                "Product not found",
                getResponse.jsonPath().getString("detail")
        );
    }

    @Test
    void getProductsShouldReturn401ForInvalidToken() {

        String invalidToken = "invalid-token";

        Response response =
                productClient.getAllProducts(invalidToken);

        assertEquals(401, response.statusCode());
    }

    @Test
    void createProductShouldReturn422WhenNameIsMissing() {

        String accessToken = getAccessToken();

        String supplierName =
                "Missing Name Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String requestBody =
                TestData.productRequestWithoutName(supplierId);

        Response response =
                productClient.createProduct(
                        accessToken,
                        requestBody
                );

        assertEquals(422, response.statusCode());
    }

    @Test
    void createProductShouldValidateNegativePrice() {

        String accessToken = getAccessToken();

        String supplierName =
                "Negative Price Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String productRequest = """
    {
      "name": "Negative Price Product %s",
      "description": "Product with negative price",
      "category": "TestCategory",
      "price": -100,
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(
                System.currentTimeMillis(),
                supplierId
        );

        Response response =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(422, response.statusCode());
    }

    @Test
    void createProductShouldValidateNegativeStockQuantity() {

        String accessToken = getAccessToken();

        String supplierName =
                "Negative Stock Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String productRequest = """
    {
      "name": "Negative Stock Product %s",
      "description": "Product with negative stock quantity",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": -10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(
                System.currentTimeMillis(),
                supplierId
        );

        Response response =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(422, response.statusCode());
    }

    @Test
    void createProductShouldValidateZeroPrice() {

        String accessToken = getAccessToken();

        String supplierName =
                "Zero Price Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String productRequest = """
    {
      "name": "Zero Price Product %s",
      "description": "Product with zero price",
      "category": "TestCategory",
      "price": 0,
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(
                System.currentTimeMillis(),
                supplierId
        );

        Response response =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(422, response.statusCode());
    }

    @Test
    void createProductShouldValidateZeroStockQuantity() {

        String accessToken = getAccessToken();

        String supplierName =
                "Zero Stock Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String productRequest = """
    {
      "name": "Zero Stock Product %s",
      "description": "Product with zero stock quantity",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": 0,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(
                System.currentTimeMillis(),
                supplierId
        );

        Response response =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        assertEquals(201, response.statusCode());
    }

    @Test
    void createProductShouldValidateInvalidPriceType() {

        String accessToken = getAccessToken();

        String supplierName =
                "Invalid Price Type Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        assertEquals(201, supplierResponse.statusCode());

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String productRequest = """
    {
      "name": "Invalid Price Type Product %s",
      "description": "Product with invalid price type",
      "category": "TestCategory",
      "price": "abc",
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(
                System.currentTimeMillis(),
                supplierId
        );

        Response response =
                productClient.createProduct(
                        accessToken,
                        productRequest
                );

        System.out.println("INVALID PRICE TYPE RESPONSE:");
        System.out.println(response.asPrettyString());

        System.out.println(
                "INVALID PRICE TYPE STATUS: " + response.statusCode()
        );
    }
}