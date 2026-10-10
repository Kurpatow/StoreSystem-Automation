package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHelper {

    private static final String URL = System.getProperty(
            "db.url",
            "jdbc:postgresql://localhost:5435/strg_users_db"
    );

    private static final String USER = System.getProperty(
            "db.user",
            "storage_admin"
    );

    private static final String PASSWORD = System.getProperty(
            "db.password",
            "THw7l0bxvPPkWUhP"
    );

    public static boolean userExists(String email) {
        String query = "SELECT email FROM users WHERE email = ?";

        try (
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean waitForUser(String email, int seconds) {
        for (int i = 0; i < seconds; i++) {
            if (userExists(email)) {
                return true;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        return false;
    }
}