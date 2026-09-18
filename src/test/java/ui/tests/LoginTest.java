package ui.tests;

import org.junit.jupiter.api.Test;
import ui.pages.LoginPage;

public class LoginTest {

    @Test
    void successfulLogin() {

        new LoginPage()
                .open()
                .enterEmail("test@example.com")
                .enterPassword("Test123456")
                .clickLogin();
    }
}