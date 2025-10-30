package dataaccess;

import exception.ResponseException;
import model.UserData;
import java.sql.Connection;
import java.sql.SQLException;


public class MySqlUserDAO implements UserDAO{

    public  MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public UserData getUser(String username){
        return new UserData("temp", "temp", "temp");
    }

    public void createUser(UserData userData){

    }

    public void deleteInfo() throws DataAccessException{
        var statement1 = "TRUNCATE game_data";
        var statement2 = "TRUNCATE auth_data";
        var statement3 = "TRUNCATE user_data";

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(statement1);
        updateExecutor.executeUpdate(statement2);
        updateExecutor.executeUpdate(statement3);
    }

    public void verifyLogin(String username, String password) throws ResponseException{

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
              `whiteUsername` varchar(256) NULL,
              `blackUsername` varchar(256) NULL,
              `gameName` varchar(256) NOT NULL,
              `game`  TEXT NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername)
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
            throw new DataAccessException("failed to create database", ex);
        }
    }

}
