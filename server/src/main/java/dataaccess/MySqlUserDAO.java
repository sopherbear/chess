package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MySqlUserDAO implements UserDAO{

    public  MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public UserData getUser(String username) throws ResponseException, DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user_data WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUserData(rs);
                    }
                }
            }
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s\n", e.getMessage()));
        }
        return null;
    }

    public void createUser(UserData userData) throws DataAccessException{
        var statement = "INSERT INTO user_data (username, password, email) VALUES (?, ?, ?)";
        String encryptedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(statement, userData.username(), encryptedPassword, userData.email());
    }

    public void deleteInfo() throws DataAccessException{
        var statement = "TRUNCATE user_data";

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(statement);
    }

    public void verifyLogin(String username, String password) throws ResponseException, DataAccessException{
        var user = getUser(username);

        var correctPassword = BCrypt.checkpw(password, user.password());
        if (!correctPassword) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: incorrect password\n");
        }
    }

    public UserData readUserData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user_data (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS game_data (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName)
              )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS auth_data (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY (`authToken`),
            INDEX (username)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database\n", ex);
        }
    }

    public int getTableCount() throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM user_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return getCount(rs);
                    }
                }
            }
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read user data: %s", e.getMessage()));
        }
        throw new ResponseException(ResponseException.Code.ServerError, "Error: Unable to count user data: %s\n");
    }

    private int getCount(ResultSet rs) throws SQLException{
        return rs.getInt(1);
    }
}
