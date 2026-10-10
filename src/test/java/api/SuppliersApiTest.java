
package api;

import data.TestData;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SuppliersApiTest extends BaseApiTest {

    @Test
    void createSupplierShouldReturn201() {

        String accessToken = Allure.step(
                "Получить токен авторизации",
                this::getAccessToken
        );

        String supplierName = "Test Supplier " + System.currentTimeMillis();

        String supplierRequest = Allure.step(
                "Подготовить данные нового поставщика",
                () -> TestData.supplierRequest(supplierName)
        );

        Response response = Allure.step(
                "Создать поставщика",
                () -> productClient.createSupplier(
                        accessToken,
                        supplierRequest
                )
        );

        Allure.step(
                "Проверить статус ответа 201",
                () -> assertEquals(201, response.statusCode())
        );

        String supplierId = Allure.step(
                "Получить ID созданного поставщика",
                () -> response.jsonPath().getString("supplier_id")
        );

        Allure.step(
                "Проверить, что ID поставщика не пустой",
                () -> assertNotNull(supplierId)
        );
    }

    @Test
    void createSupplierShouldReturn422ForDuplicateName() {

        String accessToken = Allure.step(
                "Получить токен авторизации",
                this::getAccessToken
        );

        String supplierName =
                "Duplicate Supplier " + System.currentTimeMillis();

        String supplierRequest = Allure.step(
                "Подготовить данные поставщика",
                () -> TestData.supplierRequest(supplierName)
        );

        Response firstResponse = Allure.step(
                "Создать первого поставщика",
                () -> productClient.createSupplier(
                        accessToken,
                        supplierRequest
                )
        );

        Allure.step(
                "Проверить успешное создание первого поставщика",
                () -> assertEquals(201, firstResponse.statusCode())
        );

        Response secondResponse = Allure.step(
                "Повторно создать поставщика с тем же именем",
                () -> productClient.createSupplier(
                        accessToken,
                        supplierRequest
                )
        );

        Allure.step(
                "Проверить статус ответа 422 для дубликата",
                () -> assertEquals(422, secondResponse.statusCode())
        );
    }

    @Test
    void createSupplierShouldReturn422ForDuplicateNameIgnoringCase() {

        String accessToken = Allure.step(
                "Получить токен авторизации",
                this::getAccessToken
        );

        String supplierName =
                "Case Supplier " + System.currentTimeMillis();

        String supplierRequest = Allure.step(
                "Подготовить данные первого поставщика",
                () -> TestData.supplierRequest(supplierName)
        );

        Response firstResponse = Allure.step(
                "Создать первого поставщика",
                () -> productClient.createSupplier(
                        accessToken,
                        supplierRequest
                )
        );

        Allure.step(
                "Проверить успешное создание первого поставщика",
                () -> assertEquals(201, firstResponse.statusCode())
        );

        String upperCaseRequest = Allure.step(
                "Подготовить запрос с именем в верхнем регистре",
                () -> TestData.supplierRequest(supplierName.toUpperCase())
        );

        Response secondResponse = Allure.step(
                "Создать поставщика с именем в другом регистре",
                () -> productClient.createSupplier(
                        accessToken,
                        upperCaseRequest
                )
        );

        Allure.step(
                "Проверить статус ответа 422 для дубликата без учета регистра",
                () -> assertEquals(422, secondResponse.statusCode())
        );
    }
}
