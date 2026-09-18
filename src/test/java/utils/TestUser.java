package utils;

public class TestUser {


    private final String email;
    private final String password;


    private static String savedEmail;
    private static String savedPassword;


    public TestUser(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public static void save(String userEmail, String userPassword) {
        savedEmail = userEmail;
        savedPassword = userPassword;
    }


    public static String getEmail() {
        return savedEmail;
    }


    public static String getPassword() {
        return savedPassword;
    }


    public String email() {
        return email;
    }


    public String password() {
        return password;
    }
}