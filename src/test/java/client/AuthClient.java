package client;

import config.Config;

import static io.restassured.RestAssured.given;

public class AuthClient {

    public String register(String name, String email, String password) {

        return given()
                .contentType("application/json")
                .body("""
                        {
                            "name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(name, email, password))
                .when()
                .post(Config.get("auth.base.url") + "/register/")
                .then()
                .statusCode(201)
                .extract()
                .path("user.id");
    }

    public String login(String email, String password) {

        return given()
                .contentType("application/json")
                .body("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post(Config.get("auth.base.url") + "/login/")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }
}