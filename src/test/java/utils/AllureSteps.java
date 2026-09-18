package utils;

import io.qameta.allure.Step;

public class AllureSteps {


    @Step("Создать тестового пользователя")
    public static void createUser(String email) {

        System.out.println(
                "Created user: " + email
        );
    }


    @Step("Проверить пользователя в базе данных")
    public static void checkUserInDatabase(String email) {

        System.out.println(
                "Checking database for: " + email
        );
    }


    @Step("Открыть страницу регистрации")
    public static void openRegisterPage() {

        System.out.println(
                "Opening register page"
        );
    }


    @Step("Выполнить авторизацию пользователя")
    public static void loginUser(String email) {

        System.out.println(
                "Login user: " + email
        );
    }
}