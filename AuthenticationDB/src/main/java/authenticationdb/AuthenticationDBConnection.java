package authenticationdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class AuthenticationDBConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDBConnection.class);

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String SQL_DB_URL_FORMAT = "jdbc:mysql://%s/authenticationdb";
    private static final String SQL_DB_USERNAME = "root";
    private static final String SQL_DB_PASSWORD = "root";
    private static final String authDbUrl = System.getenv("AUTH_DB_URL");

    private static AuthenticationDBConnection instance = null;
    private final Connection connection;

    private AuthenticationDBConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        String sqlDbUrl = String.format(SQL_DB_URL_FORMAT, authDbUrl);
        connection = DriverManager.getConnection(sqlDbUrl, SQL_DB_USERNAME, SQL_DB_PASSWORD);
    }

    public static void initialize() throws SQLException, ClassNotFoundException {
        if (instance != null) {
            throw new IllegalStateException();
        }
        LOGGER.info("Initializing AuthenticationDB");
        instance = new AuthenticationDBConnection();
        AuthenticationDBUsersInitializer.initializeUsersTable(instance);
    }

    public static AuthenticationDBConnection getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    public int getUserType(int userID, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE userID = ? and password = ?");
        preparedStatement.setInt(1, userID);
        preparedStatement.setString(2, password);
        ResultSet rs = preparedStatement.executeQuery();
        if (!rs.next()) {
            return UserTypeConstants.USER_TYPE_ERROR;
        }
        return rs.getInt("userType");
    }

    public void registerUser(String username, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (username, password, userType) VALUES (?, ?, ?);");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setInt(3, UserTypeConstants.USER_TYPE_NOT_ACCEPTED);
        preparedStatement.executeUpdate();
    }

    public void acceptUser(int userID) {
        // TODO
    }

    void executeUpdate(String update) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(update);
    }

}
