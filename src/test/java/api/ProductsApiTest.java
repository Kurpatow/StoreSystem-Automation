package api;


import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class ProductsApiTest {

    @Test
    void getProductsShouldReturn200ForAuthorizedUser() {

        String email = "test" + System.currentTimeMillis() + "@example.com";
        String password = "Test123456";
        String name = "Test User";

        // 1. Register
        given()
                .contentType("application/json")
                .body("""
                        {
                            "name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(name, email, password))
                .when()
                .post("http://localhost:8001/register/")
                .then()
                .statusCode(201);

        // 2. Login and get token
        String accessToken =
                given()
                        .contentType("application/json")
                        .body("""
                                {
                                    "email": "%s",
                                    "password": "%s"
                                }
                                """.formatted(email, password))
                        .when()
                        .post("http://localhost:8001/login/")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("access_token");

        // 3. Get products with token
        given()
                .auth()
                .oauth2(accessToken)
                .when()
                .get("http://localhost:8002/products/")
                .then()
                .statusCode(200);
    }
}