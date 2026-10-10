
package ui.tests;

import config.SelenideConfig;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.LoginPage;
import ui.pages.RegisterPage;
import utils.AllureScreenshotExtension;
import utils.DatabaseHelper;
import utils.TestData;
import utils.TestUser;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Store System")
@Feature("Authentication")
@ExtendWith(AllureScreenshotExtension.class)
public class LoginTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    @Story("Successful Login")
    void successfulLogin() {

        TestUser user = Allure.step(
                "Сгенерировать данные тестового пользователя",
                TestData::createUser
        );

        String email = user.email();
        String password = user.password();

        Allure.step("Сохранить данные пользователя", () ->
                TestUser.save(email, password)
        );

        RegisterPage registerPage = new RegisterPage();

        Allure.step("Открыть страницу регистрации", registerPage::open);

        Allure.step("Заполнить форму регистрации", () -> {
            registerPage
                    .enterUsername("Auto User")
                    .enterEmail(email)
                    .enterPassword(password)
                    .clickRegister();
        });

        boolean userCreated = Allure.step(
                "Проверить создание пользователя в базе данных",
                () -> DatabaseHelper.waitForUser(email, 10)
        );

        Allure.step("Убедиться, что пользователь зарегистрирован", () ->
                assertTrue(
                        userCreated,
                        "Пользователь не найден в базе"
                )
        );

        LoginPage loginPage = new LoginPage();

        Allure.step("Открыть страницу авторизации", loginPage::open);

        Allure.step("Ввести email пользователя", () ->
                loginPage.enterEmail(email)
        );

        Allure.step("Ввести пароль пользователя", () ->
                loginPage.enterPassword(password)
        );

        Allure.step("Нажать кнопку входа", loginPage::clickLogin);

        String currentUrl = Allure.step(
                "Получить URL после авторизации",
                loginPage::getCurrentUrl
        );

        Allure.step("Зафиксировать URL после авторизации", () ->
                Allure.addAttachment(
                        "URL после авторизации",
                        currentUrl == null ? "URL отсутствует" : currentUrl
                )
        );
    }
}
