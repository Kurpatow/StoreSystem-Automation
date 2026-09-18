package ui.tests;

import config.SelenideConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.pages.LoginPage;
import utils.TestUser;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class LoginTest {


    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }


    @Test
    void successfulLogin() {


        LoginPage loginPage =
                new LoginPage();


        loginPage
                .open()
                .enterEmail(TestUser.getEmail())
                .enterPassword(TestUser.getPassword())
                .clickLogin();


        assertTrue(
                loginPage.isLoginSuccessful(),
                "Авторизация не выполнена"
        );
    }
}