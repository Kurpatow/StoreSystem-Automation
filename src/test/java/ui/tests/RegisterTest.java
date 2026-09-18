package ui.tests;

import config.SelenideConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.pages.RegisterPage;
import utils.DatabaseHelper;
import utils.TestData;
import utils.TestUser;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    void successfulRegistration() {

        TestUser user = TestData.createUser();

        String email = user.email();
        String password = user.password();

        System.out.println("REGISTER EMAIL: " + email);
        System.out.println("REGISTER PASSWORD: " + password);

        TestUser.save(email, password);


        RegisterPage registerPage = new RegisterPage();

        registerPage
                .open()
                .enterUsername("Auto User")
                .enterEmail(email)
                .enterPassword(password)
                .clickRegister();

        boolean userCreated =
                DatabaseHelper.waitForUser(email, 5);

        assertTrue(
                userCreated,
                "Пользователь не найден в базе"
        );

        String currentUrl = registerPage.getCurrentUrl();

        System.out.println("CURRENT URL: " + currentUrl);
    }
}