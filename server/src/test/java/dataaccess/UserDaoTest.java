package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {
    private static UserDAO userMemory = new MemoryUserDAO();

    @BeforeEach
    void clearTests() throws DataAccessException {
        deleteUserTable();
        var command =
                """
                            CREATE TABLE IF NOT EXISTS  user_data (
                              `username` varchar(256) NOT NULL,
                              `password` varchar(256) NOT NULL,
                              `email` varchar(256) NOT NULL,
                            PRIMARY KEY (`username`),
                            INDEX(password)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                        """;
        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(command);
        userMemory.deleteInfo();
    }

    @Test
    public void deleteInfoPositive() throws DataAccessException, ResponseException {
        List<UserDAO> userDaos = List.of(new MySqlUserDAO(), userMemory);

        for (UserDAO userDao : userDaos) {
            userDao.createUser(new UserData("Mike", "password", "email.com"));
            userDao.deleteInfo();
            assertTrue(userDao.getTableCount() == 0);
        }
    }

    @Test
    public void createUserPositive() throws DataAccessException, ResponseException {
        List<UserDAO> userDaos = List.of(new MySqlUserDAO(), userMemory);

        for (UserDAO userDao : userDaos) {
            userDao.createUser(new UserData("Mike", "password", "email.com"));
            userDao.createUser(new UserData("Steve", "theHair", "Harrington@gmail.com"));
            assertTrue(userDao.getTableCount() == 2);
        }
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        var userSql = new MySqlUserDAO();
        deleteUserTable();

        assertThrows(DataAccessException.class, () ->
                userSql.createUser(new UserData("Demagorgon", "up$ideDoWN", "blaaaargh.com")));
    }

    @Test
    public void getUserPositive() throws DataAccessException, ResponseException {
        List<UserDAO> userDaos = List.of(new MySqlUserDAO(), userMemory);

        for (UserDAO userDao : userDaos) {
            userDao.createUser(new UserData("Mike", "password", "email.com"));
            userDao.createUser(new UserData("Steve", "theHair", "Harrington@gmail.com"));
            var user = userDao.getUser("Steve");
            assertTrue(user.email().equals("Harrington@gmail.com"));
        }
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        var userSql = new MySqlUserDAO();
        deleteUserTable();
        assertThrows(ResponseException.class, () ->
                userSql.getUser("Steve"));
    }

    @Test
    public void verifyLoginPositive() throws DataAccessException, ResponseException {
        List<UserDAO> userDaos = List.of(new MySqlUserDAO(), userMemory);

        for (UserDAO userDao : userDaos) {
            userDao.createUser(new UserData("Mike", "password", "email.com"));
            userDao.verifyLogin("Mike", "password");
            userDao.verifyLogin("Mike", "password");
            assertTrue(userDao.getTableCount() == 1);
        }
    }

    @Test
    public void verifyLoginNegative() throws DataAccessException, ResponseException {
        List<UserDAO> userDaos = List.of(new MySqlUserDAO(), userMemory);

        for (UserDAO userDao : userDaos) {
            userDao.createUser(new UserData("Mike", "password", "email.com"));
            userDao.verifyLogin("Mike", "password");
            assertThrows(ResponseException.class, () ->
                    userDao.verifyLogin("Mike", "theHair"));
        }
    }

    @Test
    public void getTableCountPositive() throws DataAccessException, ResponseException {
        List<UserDAO> userDaos = List.of(new MySqlUserDAO(), userMemory);

        for (UserDAO userDao : userDaos) {
            userDao.createUser(new UserData("Mike", "password", "email.com"));
            userDao.createUser(new UserData("El", "waffles", "emel.com"));
            assertTrue(userDao.getTableCount() == 2);
        }
    }

    @Test
    public void getTableCountNegative() throws DataAccessException {
        var userDao = new MySqlUserDAO();
        deleteUserTable();
        assertThrows(ResponseException.class, () ->
                userDao.getTableCount());
    }

    private void deleteUserTable() throws DataAccessException {
        var command = "DROP TABLE IF EXISTS user_data";
        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(command);
    }
}
