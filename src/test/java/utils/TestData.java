package utils;

public class TestData {


    public static String generateEmail() {
        return "auto" + System.currentTimeMillis() + "@test.com";
    }


    public static String password() {
        return "Test123456";
    }


    public static TestUser createUser() {

        String email = generateEmail();
        String password = password();

        TestUser.save(email, password);

        return new TestUser(
                email,
                password
        );
    }
}