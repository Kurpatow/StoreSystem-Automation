
package ui.tests;

import config.SelenideConfig;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.pages.RegisterPage;
import utils.DatabaseHelper;
import utils.TestData;
import utils.TestUser;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Store System")
@Feature("Authentication")
public class RegisterTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    @Story("Successful Registration")
    void successfulRegistration() {

        TestUser user = Allure.step(
                "Сгенерировать данные нового пользователя",
                TestData::createUser
        );

        String email = Allure.step(
                "Получить email пользователя",
                user::email
        );

        String password = Allure.step(
                "Получить пароль пользователя",
                user::password
        );

        Allure.step("Сохранить данные пользователя для авторизации", () ->
                TestUser.save(email, password)
        );

        RegisterPage registerPage = Allure.step(
                "Создать страницу регистрации",
                RegisterPage::new
        );

        Allure.step("Открыть страницу регистрации", registerPage::open);

        Allure.step("Ввести имя пользователя", () ->
                registerPage.enterUsername("Auto User")
        );

        Allure.step("Ввести email пользователя", () ->
                registerPage.enterEmail(email)
        );

        Allure.step("Ввести пароль пользователя", () ->
                registerPage.enterPassword(password)
        );

        Allure.step("Отправить форму регистрации", registerPage::clickRegister);

        boolean userCreated = Allure.step(
                "Проверить наличие пользователя в базе данных",
                () -> DatabaseHelper.waitForUser(email, 5)
        );

        Allure.step("Проверить успешное создание пользователя в базе данных", () ->
                assertTrue(
                        userCreated,
                        "Пользователь не найден в базе"
                )
        );

        String currentUrl = Allure.step(
                "Получить URL после регистрации",
                registerPage::getCurrentUrl
        );

        Allure.step("Зафиксировать URL после регистрации", () ->
                Allure.addAttachment(
                        "URL после регистрации",
                        currentUrl == null ? "URL отсутствует" : currentUrl
                )
        );
    }
}
