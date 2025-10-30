package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.eclipse.jetty.server.Response;

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

    public void deleteInfo(){

    }

    public void verifyLogin(String username, String password) throws ResponseException{

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
