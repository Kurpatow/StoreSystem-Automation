package api;

import client.AuthClient;
import client.ProductClient;

public class BaseApiTest {

    protected final AuthClient authClient = new AuthClient();
    protected final ProductClient productClient = new ProductClient();

    protected String getAccessToken() {

        String email = "test" + System.currentTimeMillis() + "@example.com";
        String password = "Test123456";
        String name = "Test User";

        authClient.register(name, email, password);

        return authClient.login(email, password);
    }
}