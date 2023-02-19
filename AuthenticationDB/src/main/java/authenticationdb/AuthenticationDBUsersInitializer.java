package authenticationdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static authenticationdb.UserTypeConstants.USER_TYPE_ADMIN;

public class AuthenticationDBUsersInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDBUsersInitializer.class);

    private static final String CREATE_USERS_TABLE_QUERY =
            "CREATE TABLE IF NOT EXISTS `users` (\n" +
            "`userID` INT unsigned NOT NULL AUTO_INCREMENT,\n" +
            "`username` VARCHAR(64) NOT NULL,\n" +
            "`password` VARCHAR(64) NOT NULL,\n" +
            "`userType` INT unsigned NOT NULL,\n" +
            "UNIQUE (`username`),\n" +
            "PRIMARY KEY (`userID`)\n" +
            ");";

    private static final String INSERT_ADMIN_QUERY =
            "INSERT INTO users (username, password, userType)\n" +
            "VALUES ('admin', 'adminPassword', '" + USER_TYPE_ADMIN + "');";

    static void initializeUsersTable(AuthenticationDBConnection authenticationDBConnection) {
        try {
            LOGGER.info("Initializing AuthenticationDB users table");
            authenticationDBConnection.executeUpdate(CREATE_USERS_TABLE_QUERY);
            authenticationDBConnection.executeUpdate(INSERT_ADMIN_QUERY);
        } catch (SQLException e) {
            LOGGER.info(e.toString());
        }
    }

}
