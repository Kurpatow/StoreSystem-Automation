package ui.pages;

import com.codeborne.selenide.SelenideElement;

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
                "http://localhost:8001/login/"
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

    public void clickLogin() {
        loginButton.click();
    }
}