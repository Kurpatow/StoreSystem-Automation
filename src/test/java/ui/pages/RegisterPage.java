package ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

    private final SelenideElement usernameInput =
            $("input[placeholder='Имя пользователя']");

    private final SelenideElement emailInput =
            $("input[type='email']");

    private final SelenideElement passwordInput =
            $("input[type='password']");

    private final SelenideElement registerButton =
            $("button[type='submit']");


    public RegisterPage open() {
        com.codeborne.selenide.Selenide.open(
                "http://host.docker.internal:8001/register/"
        );
        return this;
    }

    public RegisterPage enterUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }


    public RegisterPage enterEmail(String email) {
        emailInput.setValue(email);
        return this;
    }


    public RegisterPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }


    public RegisterPage clickRegister() {
        registerButton.click();
        return this;
    }

    public String getCurrentUrl() {
        return WebDriverRunner.url();
    }
}