package api;

import client.AuthClient;

public class BaseApiTest {

    protected final AuthClient authClient = new AuthClient();

    protected String getAccessToken() {

        String email = "test" + System.currentTimeMillis() + "@example.com";
        String password = "Test123456";
        String name = "Test User";

        authClient.register(name, email, password);

        return authClient.login(email, password);
    }
}
