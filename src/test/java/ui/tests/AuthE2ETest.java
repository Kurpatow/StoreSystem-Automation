
package ui.tests;

import config.SelenideConfig;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Description;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.pages.LoginPage;
import ui.pages.RegisterPage;
import utils.AllureSteps;
import utils.DatabaseHelper;
import utils.TestData;
import utils.TestUser;

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
    @Description("Регистрация нового пользователя и последующая авторизация")
    void registerAndLoginFlow() {

        String email = Allure.step(
                "Сгенерировать email нового пользователя",
                TestData::generateEmail
        );

        String password = Allure.step(
                "Подготовить пароль пользователя",
                TestData::password
        );

        AllureSteps.createUser(email);

        Allure.step(
                "Сохранить данные пользователя для дальнейшего использования",
                () -> TestUser.save(email, password)
        );

        Allure.step(
                "Открыть страницу регистрации",
                AllureSteps::openRegisterPage
        );

        RegisterPage registerPage = new RegisterPage();

        Allure.step("Зарегистрировать нового пользователя", () -> {
            registerPage
                    .open()
                    .enterUsername("Auto User")
                    .enterEmail(email)
                    .enterPassword(password)
                    .clickRegister();
        });

        AllureSteps.checkUserInDatabase(email);

        boolean userExists = Allure.step(
                "Проверить наличие пользователя в базе данных",
                () -> DatabaseHelper.waitForUser(email, 10)
        );

        Allure.step(
                "Убедиться, что пользователь сохранён в базе данных",
                () -> assertTrue(
                        userExists,
                        "Пользователь не найден в базе"
                )
        );

        AllureSteps.loginUser(email);

        LoginPage loginPage = new LoginPage();

        Allure.step("Выполнить авторизацию пользователя", () -> {
            loginPage
                    .open()
                    .enterEmail(email)
                    .enterPassword(password)
                    .clickLogin();
        });

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
