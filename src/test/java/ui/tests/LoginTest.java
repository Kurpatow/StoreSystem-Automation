
package ui.tests;

import com.codeborne.selenide.Selenide;
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
import client.AuthClient;

import static org.junit.jupiter.api.Assertions.*;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

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

        String accessToken = Allure.step(
                "Проверить email и пароль через API",
                () -> new AuthClient().login(email, password)
        );

        assertNotNull(
                accessToken,
                "Сервер авторизации не вернул access_token"
        );

        LoginPage loginPage = new LoginPage();

        Allure.step("Открыть страницу авторизации", loginPage::open);

        Allure.step("Ввести email пользователя", () ->
                loginPage.enterEmail(email)
        );

        Allure.step("Ввести пароль пользователя", () ->
                loginPage.enterPassword(password)
        );


        Allure.step("Нажать кнопку входа", () -> {
            loginPage.clickLogin();

            String diagnostics = Selenide.executeJavaScript("""
        return JSON.stringify({
            url: window.location.href,
            userId: localStorage.getItem('user_id'),
            refreshTokenPresent: document.cookie.includes('refresh_token='),
            pageText: document.body.innerText
        }, null, 2);
        """);

            System.out.println("=== ДИАГНОСТИКА АВТОРИЗАЦИИ ===");
            System.out.println(diagnostics);
            System.out.println("=== КОНЕЦ ДИАГНОСТИКИ ===");
        });



        String outcome = Allure.step("Дождаться результата авторизации", () -> {
            WebDriver driver = WebDriverRunner.getWebDriver();
            WebDriverWait wait = new WebDriverWait(
                    driver,
                    Duration.ofSeconds(10)
            );

            return wait.until(d -> {
                String url = d.getCurrentUrl();

                if (url.contains("/products")) {
                    return "SUCCESS: " + url;
                }

                try {
                    Alert alert = d.switchTo().alert();
                    String message = alert.getText();
                    alert.accept();
                    return "LOGIN_ERROR: " + message;
                } catch (NoAlertPresentException ignored) {
                    return null;
                }
            });
        });

        System.out.println("LOGIN_OUTCOME = " + outcome);

        Allure.addAttachment("Результат авторизации", outcome);

        assertTrue(
                outcome.startsWith("SUCCESS:"),
                "Авторизация не выполнена: " + outcome
        );


        Allure.addAttachment("Результат авторизации", outcome);

        assertTrue(
                outcome.startsWith("SUCCESS:"),
                "Авторизация не выполнена: " + outcome
        );

        String productsPageText = Selenide.executeJavaScript(
                "return document.body.innerText"
        );

        System.out.println("=== СТРАНИЦА ТОВАРОВ ===");
        System.out.println("URL = " + WebDriverRunner.url());
        System.out.println(productsPageText);
        System.out.println("=== КОНЕЦ СТРАНИЦЫ ТОВАРОВ ===");

        assertTrue(
                WebDriverRunner.url().contains("/products"),
                "После входа браузер не находится на странице товаров"
        );

        assertFalse(
                productsPageText.isBlank(),
                "Страница товаров загрузилась, но её содержимое пустое"
        );
    }
}
