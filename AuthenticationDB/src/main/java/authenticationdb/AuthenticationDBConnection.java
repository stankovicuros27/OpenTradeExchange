package authenticationdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationDBConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDBConnection.class);

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String SQL_DB_URL_FORMAT = "jdbc:mysql://%s/authenticationdb";
    private static final String SQL_DB_USERNAME = "root";
    private static final String SQL_DB_PASSWORD = "root";
    private static final String AUTH_DB_URL = System.getenv("AUTH_DB_URL");

    private static AuthenticationDBConnection instance = null;

    private final Connection connection;

    private AuthenticationDBConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        String sqlDbUrl = String.format(SQL_DB_URL_FORMAT, AUTH_DB_URL);
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

    public synchronized void registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password, userType) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, UserTypeConstants.USER_TYPE_NOT_ACCEPTED);
            preparedStatement.executeUpdate();
        }
    }

    public synchronized void deleteUser(int userID) throws SQLException {
        String sql = "DELETE FROM users WHERE userID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.executeUpdate();
        }
    }

    public synchronized int getUserType(int userID, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE userID = ? and password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, password);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return UserTypeConstants.USER_NOT_FOUND;
                }
                return rs.getInt("userType");
            }
        }
    }

    public synchronized void setUserType(int userID, int userType) throws SQLException {
        String sql = "UPDATE users SET userType = ? WHERE userID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userType);
            preparedStatement.setInt(2, userID);
            preparedStatement.executeUpdate();
        }
    }

    public synchronized boolean isUsernameExists(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public synchronized List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    users.add(new User(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4)
                    ));
                }
            }
        }
        return users;
    }

    synchronized void executeUpdate(String update) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(update);
        }
    }

}
