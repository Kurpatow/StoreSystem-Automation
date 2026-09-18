package ui.tests;

import config.SelenideConfig;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.pages.LoginPage;
import ui.pages.RegisterPage;
import utils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Store System")
@Feature("Authentication")
public class AuthE2ETest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    @Story("Register and Login")
    @Description(
            "Регистрация нового пользователя и последующая авторизация"
    )
    void registerAndLoginFlow() {

        // Генерируем пользователя
        String email = TestData.generateEmail();
        String password = TestData.password();

        AllureSteps.createUser(email);

        System.out.println("EMAIL: " + email);
        System.out.println("PASSWORD: " + password);

        TestUser.save(
                email,
                password
        );

        // Регистрация
        AllureSteps.openRegisterPage();

        RegisterPage registerPage =
                new RegisterPage();

        registerPage
                .open()
                .enterUsername("Auto User")
                .enterEmail(email)
                .enterPassword(password)
                .clickRegister();

        // Проверяем БД
        AllureSteps.checkUserInDatabase(email);

        boolean userExists =
                DatabaseHelper.waitForUser(
                        email,
                        10
                );

        assertTrue(
                userExists,
                "Пользователь не найден в базе"
        );

        // Авторизация
        AllureSteps.loginUser(email);

        LoginPage loginPage =
                new LoginPage();

        loginPage
                .open()
                .enterEmail(email)
                .enterPassword(password)
                .clickLogin();

        System.out.println(
                "LOGIN URL: "
                        + loginPage.getCurrentUrl()
        );
    }
}