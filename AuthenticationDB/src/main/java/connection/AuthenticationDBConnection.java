package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AuthenticationDBConnection {

    private static final String SQL_DB_URL_FORMAT = "jdbc:mysql://%s/authenticationdb";
    private static final String SQL_DB_USERNAME = "root";
    private static final String SQL_DB_PASSWORD = "root";

    private final String authDbUrl = System.getenv("AUTH_DB_URL");

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String sqlDbUrl = String.format(SQL_DB_URL_FORMAT, authDbUrl);
            Connection connection = DriverManager.getConnection(sqlDbUrl, SQL_DB_USERNAME, SQL_DB_PASSWORD);
            System.out.println("Good");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Bad");
            throw new RuntimeException(e);
        }
    }

}
