package ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement emailInput =
            $("input[type='email']");

    private final SelenideElement passwordInput =
            $("input[type='password']");

    private final SelenideElement loginButton =
            $("button[type='submit']");


    public LoginPage open() {
        com.codeborne.selenide.Selenide.open(
                "http://host.docker.internal:8001/login/"
        );
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

    public boolean isLoginSuccessful() {

        return !com.codeborne.selenide.Selenide
                .title()
                .isEmpty();
    }

    public String getCurrentUrl() {
        return WebDriverRunner.url();
    }
}