package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseHelper {


    private static final String URL =
            "jdbc:postgresql://localhost:5435/strg_users_db";

    private static final String USER =
            "storage_admin";

    private static final String PASSWORD =
            "THw7l0bxvPPkWUhP";


    public static boolean userExists(String email) {


        String query =
                "SELECT email FROM users WHERE email='" + email + "'";


        try (
                Connection connection =
                        DriverManager.getConnection(
                                URL,
                                USER,
                                PASSWORD
                        );

                Statement statement =
                        connection.createStatement();

                ResultSet result =
                        statement.executeQuery(query)

        ) {

            return result.next();

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
                throw new RuntimeException(e);
            }
        }

        return false;
    }
}