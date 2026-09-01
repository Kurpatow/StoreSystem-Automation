package api;

import client.AuthClient;
import config.Config;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class ProductsApiTest {

    @Test
    void getProductsShouldReturn200ForAuthorizedUser() {

        String email = "test" + System.currentTimeMillis() + "@example.com";
        String password = "Test123456";
        String name = "Test User";

        AuthClient authClient = new AuthClient();

        // 1. Register
        authClient.register(name, email, password);

        // 2. Login
        String accessToken = authClient.login(email, password);

        // 3. Get products
        given()
                .auth()
                .oauth2(accessToken)
                .when()
                .get(Config.get("products.base.url") + "/products/")
                .then()
                .statusCode(200);
    }
}