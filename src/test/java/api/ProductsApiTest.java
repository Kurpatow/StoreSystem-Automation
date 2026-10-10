package api;

import data.TestData;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Store System")
@Feature("Products API")
class ProductsApiTest extends BaseApiTest {

    @Test
    @Story("Получение и изменение товара")
    @Description("Создание поставщика и товара, получение товара по ID, изменение доступности и обновление данных")
    void getProductByIdShouldReturn200ForAuthorizedUser() {

        String accessToken = getAccessToken();

        String supplierName =
                "Test Supplier " + System.currentTimeMillis();

        String supplierRequest =
                TestData.supplierRequest(supplierName);

        Response supplierResponse = Allure.step(
                "Создать поставщика",
                () -> productClient.createSupplier(
                        accessToken,
                        supplierRequest
                )
        );

        System.out.println("SUPPLIER RESPONSE:");
        System.out.println(supplierResponse.asPrettyString());

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        System.out.println("SUPPLIER ID: " + supplierId);

        Allure.step("Проверить наличие ID поставщика", () ->
                assertEquals(false, supplierId == null)
        );

        String productName =
                "Test Product " + System.currentTimeMillis();

        String productRequest =
                TestData.productRequest(
                        productName,
                        supplierId
                );

        Response productResponse = Allure.step(
                "Создать товар",
                () -> productClient.createProduct(
                        accessToken,
                        productRequest
                )
        );

        System.out.println("PRODUCT RESPONSE:");
        System.out.println(productResponse.asPrettyString());

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, productResponse.statusCode())
        );

        String productId =
                productResponse.jsonPath().getString("product_id");

        System.out.println("PRODUCT ID: " + productId);

        assertEquals(false, productId == null);

        Response getProductResponse = Allure.step(
                "Получить товар по ID",
                () -> productClient.getProductById(
                        accessToken,
                        productId
                )
        );

        System.out.println("GET PRODUCT RESPONSE:");
        System.out.println(getProductResponse.asPrettyString());

        Allure.step("Проверить успешный ответ (200)", () ->
                assertEquals(200, getProductResponse.statusCode())
        );

        assertEquals(
                productId,
                getProductResponse.jsonPath().getString("product_id")
        );

        Response updateAvailabilityResponse = Allure.step(
                "Изменить доступность товара",
                () -> productClient.updateAvailability(
                        accessToken,
                        productId,
                        true
                )
        );

        System.out.println("UPDATE AVAILABILITY RESPONSE:");
        System.out.println(updateAvailabilityResponse.asPrettyString());

        Allure.step("Проверить успешный ответ (200)", () ->
                assertEquals(200, updateAvailabilityResponse.statusCode())
        );

        Response updatedProductResponse = Allure.step(
                "Получить товар и проверить доступность",
                () -> productClient.getProductById(
                        accessToken,
                        productId
                )
        );

        System.out.println("UPDATED PRODUCT RESPONSE:");
        System.out.println(updatedProductResponse.asPrettyString());

        Allure.step("Проверить успешный ответ (200)", () ->
                assertEquals(200, updatedProductResponse.statusCode())
        );

        assertEquals(
                true,
                updatedProductResponse.jsonPath().getBoolean("is_available")
        );

        String updatedProductName =
                "Updated Product " + System.currentTimeMillis();

        String updateProductRequest =
                TestData.updatedProductRequest(
                        updatedProductName,
                        supplierId
                );

        Response updateProductResponse = Allure.step(
                "Обновить данные товара",
                () -> productClient.updateProduct(
                        accessToken,
                        productId,
                        updateProductRequest
                )
        );

        System.out.println("UPDATE PRODUCT RESPONSE:");
        System.out.println(updateProductResponse.asPrettyString());

        Allure.step("Проверить успешный ответ (200)", () ->
                assertEquals(200, updateProductResponse.statusCode())
        );

        Response getUpdatedProductResponse = Allure.step(
                "Получить товар и проверить обновлённые данные",
                () -> productClient.getProductById(
                        accessToken,
                        productId
                )
        );

        System.out.println("GET UPDATED PRODUCT RESPONSE:");
        System.out.println(getUpdatedProductResponse.asPrettyString());

        Allure.step("Проверить успешный ответ (200)", () ->
                assertEquals(200, getUpdatedProductResponse.statusCode())
        );

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
    @Story("Авторизация")
    @Description("Получение списка товаров без токена должно возвращать 401")
    void getProductsShouldReturn401WithoutAuthorization() {

        Response response = Allure.step(
                "Получить товары без авторизации",
                () -> productClient.getAllProducts("")
        );

        Allure.step("Проверить статус ответа 401", () ->
                assertEquals(401, response.statusCode())
        );
    }

    @Test
    @Story("Авторизация")
    @Description("Создание товара без токена должно возвращать 401")
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

        Response response = Allure.step(
                "Создать товар без авторизации",
                () -> productClient.createProduct("", productRequest)
        );

        Allure.step("Проверить статус ответа 401", () ->
                assertEquals(401, response.statusCode())
        );
    }

    @Test
    @Story("Валидация товара")
    @Description("Повторное создание товара с тем же названием должно возвращать 422")
    void createProductShouldReturn422ForDuplicateName() {

        String accessToken = getAccessToken();

        String supplierName =
                "Duplicate Product Supplier " + System.currentTimeMillis();

        Response supplierResponse = Allure.step(
                "Создать поставщика",
                () -> productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                )
        );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        Allure.step("Проверить наличие ID поставщика", () ->
                assertEquals(false, supplierId == null)
        );

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

        Response firstResponse = Allure.step(
                "Создать первый товар",
                () -> productClient.createProduct(
                        accessToken,
                        productRequest
                )
        );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, firstResponse.statusCode())
        );

        Response secondResponse = Allure.step(
                "Повторно создать товар с тем же названием",
                () -> productClient.createProduct(
                        accessToken,
                        productRequest
                )
        );

        Allure.step("Проверить ошибку валидации (422)", () ->
                assertEquals(422, secondResponse.statusCode())
        );
    }

    @Test
    @Story("Валидация товара")
    @Description("Название товара должно проверяться без учёта регистра")
    void createProductShouldReturn422ForDuplicateNameIgnoringCase() {

        String accessToken = getAccessToken();

        String supplierName =
                "Case Test Supplier " + System.currentTimeMillis();

        Response supplierResponse = Allure.step(
                "Создать поставщика",
                () -> productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                )
        );

        Allure.step("Проверить создание поставщика", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        Allure.step("Проверить наличие ID поставщика", () ->
                assertEquals(false, supplierId == null)
        );

        String productName =
                "Case Test Product " + System.currentTimeMillis();

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

        Response firstResponse = Allure.step(
                "Создать первый товар",
                () -> productClient.createProduct(
                        accessToken,
                        firstProductRequest
                )
        );

        Allure.step("Проверить создание первого товара", () ->
                assertEquals(201, firstResponse.statusCode())
        );

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

        Response secondResponse = Allure.step(
                "Создать товар с тем же названием в другом регистре",
                () -> productClient.createProduct(
                        accessToken,
                        secondProductRequest
                )
        );

        Allure.step("Проверить отказ при дублировании названия", () ->
                assertEquals(422, secondResponse.statusCode())
        );
    }

    @Test
    @Story("Валидация поставщика")
    @Description("Создание товара с несуществующим поставщиком должно возвращать 404")
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
                productClient.createProduct(accessToken, productRequest);

        Allure.step("Проверить ответ «не найдено» (404)", () ->
                assertEquals(404, response.statusCode())
        );

        assertEquals(
                "Supplier not found",
                response.jsonPath().getString("detail")
        );
    }

    @Test
    @Story("Валидация поставщика")
    @Description("Некорректный UUID поставщика должен возвращать 422")
    void createProductShouldReturn422ForInvalidSupplierId() {

        String accessToken = getAccessToken();

        String productRequest = """
                {
                  "name": "Invalid Supplier ID Product %s",
                  "description": "Product with invalid supplier ID",
                  "category": "TestCategory",
                  "price": 100,
                  "stock_quantity": 10,
                  "supplier_id": "abc",
                  "image_url": "https://example.com/product.jpg",
                  "weight": 1.5,
                  "dimensions": "10x10x10",
                  "manufacturer": "TestManufacturer"
                }
                """.formatted(System.currentTimeMillis());

        Response response =
                productClient.createProduct(accessToken, productRequest);

        Allure.step("Проверить ошибку валидации (422)", () ->
                assertEquals(422, response.statusCode())
        );
    }

    @Test
    @Story("Валидация ID")
    @Description("Некорректный ID товара должен возвращать 400")
    void getProductByIdShouldReturn400ForInvalidProductId() {

        String accessToken = getAccessToken();

        Response response =
                productClient.getProductById(accessToken, "abc");

        Allure.step("Проверить ошибку некорректного ID (400)", () ->
                assertEquals(400, response.statusCode())
        );
    }

    @Test
    @Story("Авторизация")
    @Description("Авторизованный пользователь может получить список товаров")
    void getProductsShouldReturn200ForAuthorizedUser() {

        String accessToken = getAccessToken();

        Response response =
                productClient.getAllProducts(accessToken);

        Allure.step("Проверить успешный ответ (200)", () ->
                assertEquals(200, response.statusCode())
        );
    }

    @Test
    @Story("Валидация ID")
    @Description("Несуществующий товар должен возвращать 404")
    void getProductByIdShouldReturn404ForNonExistentProduct() {

        String accessToken = getAccessToken();

        String nonExistentProductId =
                "00000000-0000-0000-0000-000000000001";

        Response response = Allure.step(
                "Получить несуществующий товар по ID",
                () -> productClient.getProductById(
                        accessToken,
                        nonExistentProductId
                )
        );

        Allure.step("Проверить статус ответа 404", () ->
                assertEquals(404, response.statusCode())
        );

        Allure.step("Проверить сообщение Product not found", () ->
                assertEquals(
                        "Product not found",
                        response.jsonPath().getString("detail")
                )
        );

    }

    @Test
    @Story("Удаление товара")
    @Description("После удаления товар не должен находиться по ID")
    void deleteProductShouldReturn200() {

        String accessToken = getAccessToken();

        String supplierName =
                "Delete Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String productName =
                "Delete Product " + System.currentTimeMillis();

        Response productResponse =
                productClient.createProduct(
                        accessToken,
                        TestData.productRequest(productName, supplierId)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, productResponse.statusCode())
        );

        String productId =
                productResponse.jsonPath().getString("product_id");

        Response deleteResponse = Allure.step(
                "Удалить товар",
                () -> productClient.deleteProduct(
                        accessToken,
                        productId
                )
        );

        Allure.step("Проверить успешный ответ (200)", () ->
                assertEquals(200, deleteResponse.statusCode())
        );

        Response getResponse = Allure.step(
                "Проверить отсутствие удалённого товара",
                () -> productClient.getProductById(
                        accessToken,
                        productId
                )
        );

        Allure.step("Проверить ответ «не найдено» (404)", () ->
                assertEquals(404, getResponse.statusCode())
        );

        assertEquals(
                "Product not found",
                getResponse.jsonPath().getString("detail")
        );
    }

    @Test
    @Story("Авторизация")
    @Description("Запрос с недействительным токеном должен возвращать 401")
    void getProductsShouldReturn401ForInvalidToken() {

        Response response = Allure.step(
                "Получить список товаров с недействительным токеном",
                () -> productClient.getAllProducts("invalid-token")
        );

        Allure.step("Проверить статус ответа 401", () ->
                assertEquals(401, response.statusCode())
        );
    }

    @Test
    @Story("Валидация товара")
    @Description("Отсутствие обязательного названия товара должно возвращать 422")
    void createProductShouldReturn422WhenNameIsMissing() {

        String accessToken = getAccessToken();

        String supplierName =
                "Missing Name Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

        String supplierId =
                supplierResponse.jsonPath().getString("supplier_id");

        String requestBody =
                TestData.productRequestWithoutName(supplierId);

        Response response =
                productClient.createProduct(accessToken, requestBody);

        Allure.step("Проверить ошибку валидации (422)", () ->
                assertEquals(422, response.statusCode())
        );
    }

    @Test
    @Story("Валидация цены")
    @Description("Отрицательная цена товара должна возвращать 422")
    void createProductShouldValidateNegativePrice() {

        String accessToken = getAccessToken();

        String supplierName =
                "Negative Price Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

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
                """.formatted(System.currentTimeMillis(), supplierId);

        Response response =
                productClient.createProduct(accessToken, productRequest);

        Allure.step("Проверить ошибку валидации (422)", () ->
                assertEquals(422, response.statusCode())
        );
    }

    @Test
    @Story("Валидация остатка")
    @Description("Отрицательное количество товара на складе должно возвращать 422")
    void createProductShouldValidateNegativeStockQuantity() {

        String accessToken = getAccessToken();

        String supplierName =
                "Negative Stock Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

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
                """.formatted(System.currentTimeMillis(), supplierId);

        Response response =
                productClient.createProduct(accessToken, productRequest);

        Allure.step("Проверить ошибку валидации (422)", () ->
                assertEquals(422, response.statusCode())
        );
    }

    @Test
    @Story("Валидация цены")
    @Description("Нулевая цена товара должна возвращать 422")
    void createProductShouldValidateZeroPrice() {

        String accessToken = getAccessToken();

        String supplierName =
                "Zero Price Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

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
                """.formatted(System.currentTimeMillis(), supplierId);

        Response response =
                productClient.createProduct(accessToken, productRequest);

        Allure.step("Проверить ошибку валидации (422)", () ->
                assertEquals(422, response.statusCode())
        );
    }

    @Test
    @Story("Валидация остатка")
    @Description("Нулевое количество товара на складе допустимо")
    void createProductShouldValidateZeroStockQuantity() {

        String accessToken = getAccessToken();

        String supplierName =
                "Zero Stock Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

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
                """.formatted(System.currentTimeMillis(), supplierId);

        Response response =
                productClient.createProduct(accessToken, productRequest);

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, response.statusCode())
        );
    }

    @Test
    @Story("Валидация цены")
    @Description("Строка вместо числовой цены должна возвращать 422")
    void createProductShouldValidateInvalidPriceType() {

        String accessToken = getAccessToken();

        String supplierName =
                "Invalid Price Type Supplier " + System.currentTimeMillis();

        Response supplierResponse =
                productClient.createSupplier(
                        accessToken,
                        TestData.supplierRequest(supplierName)
                );

        Allure.step("Проверить успешное создание (201)", () ->
                assertEquals(201, supplierResponse.statusCode())
        );

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
                """.formatted(System.currentTimeMillis(), supplierId);

        Response response =
                productClient.createProduct(accessToken, productRequest);

        System.out.println("INVALID PRICE TYPE RESPONSE:");
        System.out.println(response.asPrettyString());

        System.out.println(
                "INVALID PRICE TYPE STATUS: " + response.statusCode()
        );

        Allure.step("Проверить ошибку валидации (422)", () ->
                assertEquals(422, response.statusCode())
        );
    }
}
