package api;

import client.AuthClient;
import client.ProductClient;
import io.restassured.response.Response;
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

        // 3. Get all products
        Response productsResponse =
                productClient.getAllProducts(accessToken);
        System.out.println("PRODUCTS RESPONSE:");
        System.out.println(productsResponse.asPrettyString());

        assertEquals(200, productsResponse.statusCode());

        // 4. Get product ID from the first product
        String productId =
                productsResponse
                        .jsonPath()
                        .getString("[0].product_id");

        // 5. Get product by ID
        Response productResponse =
                productClient.getProductById(accessToken, productId);

        // 6. Check response
        assertEquals(200, productResponse.statusCode());
        assertEquals(
                productId,
                productResponse.jsonPath().getString("product_id")
        );
    }
}