package ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    private final SelenideElement emailInput =
            $("#loginForm input[type='email']");

    private final SelenideElement passwordInput =
            $("#loginForm input[type='password']");

    private final SelenideElement loginButton =
            $("#loginForm button[type='submit']");

    private final SelenideElement errorMessage =
            $("#errorMessage");

    public LoginPage open() {
        com.codeborne.selenide.Selenide.open("http://host.docker.internal:8001/login/");
        return this;
    }

    public LoginPage enterEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public LoginPage clickLogin() {
        loginButton.click();
        return this;
    }

    public boolean isLoginErrorVisible() {
        return errorMessage.isDisplayed();
    }

    public String getLoginError() {
        return errorMessage.shouldBe(visible).getText();
    }

    public String getCurrentUrl() {
        return WebDriverRunner.url();
    }
}