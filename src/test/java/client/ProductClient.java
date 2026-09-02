package client;

import config.Config;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ProductClient {

    private static final String BASE_URL = "http://localhost:8002";

    public Response getAllProducts(String token) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/products/");
    }

    public Response getProductById(String token, String productId) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .pathParam("product_id", productId)
                .when()
                .get("/products/{product_id}");
    }

    public Response createProduct(String token, String requestBody) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/products/");
    }

    public Response updateProduct(
            String token,
            String productId,
            String requestBody
    ) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .pathParam("product_id", productId)
                .body(requestBody)
                .when()
                .put("/products/{product_id}");
    }

    public Response updateAvailability(
            String token,
            String productId,
            boolean isAvailable
    ) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .pathParam("product_id", productId)
                .body("""
                        {
                          "is_available": %s
                        }
                        """.formatted(isAvailable))
                .when()
                .patch("/products/{product_id}");
    }

    public Response deleteProduct(String token, String productId) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .pathParam("product_id", productId)
                .when()
                .delete("/products/{product_id}");
    }

    public Response createSupplier(String token, String requestBody) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/suppliers/");
    }
}
