package Service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthDaoTest {
    private static GameDAO gameDAO = new MemoryGameDAO();
    private static MemoryUserDAO userDAO = new MemoryUserDAO();
    private static AuthDAO authMemory = new MemoryAuthDAO();
    private static AuthDAO authSql = new MySqlAuthDAO();
    private static  UserService userService = new UserService(authSql, userDAO, gameDAO);
    private static List<AuthDAO> authDAOs = List.of(authSql, authMemory);


    @BeforeEach
    void clearTests() throws DataAccessException {
        var command = """
            CREATE TABLE IF NOT EXISTS auth_data (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY (`authToken`),
            INDEX (username)
            )
            """;
        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(command);
        userService.clear();
        authMemory.deleteInfo();
    }


    @Test
    public void deleteInfoPositive() throws DataAccessException, ResponseException{
        for (AuthDAO authDao : authDAOs) {
            authDao.createAuth(new AuthData("token", "thundercat"));
            authDao.createAuth(new AuthData("newtoken", "tame impala"));
            authDao.deleteInfo();
            assertTrue(authDao.getTableCount()== 0);
        }
    }

    @Test
    public void createAuthPositive() throws DataAccessException, ResponseException {
        for (AuthDAO authDao : authDAOs) {
            authDao.createAuth(new AuthData("singer", "Damon"));
            authDao.createAuth(new AuthData("guitar", "Graham"));
            authDao.createAuth(new AuthData("drums", "Dave"));
            assertTrue(authDao.getTableCount() == 3);
        }
    }

    @Test
    public void createAuthNegative() throws DataAccessException{
        var authDao = new MySqlAuthDAO();
        deleteAuthTable();

        assertThrows(DataAccessException.class, ()->
                authDao.createAuth(new AuthData("bass", "Alex")));
    }

    @Test
    public void getAuthPositive() throws DataAccessException, ResponseException {
        for (AuthDAO authDao : authDAOs) {
            authDao.createAuth(new AuthData("singer", "Damon"));
            authDao.createAuth(new AuthData("guitar", "Graham"));
            authDao.createAuth(new AuthData("drums", "Dave"));
            var authData = authDao.getAuth("guitar");
            assertTrue(authData.username().equals("Graham"));
        }
    }

    @Test
    public void getAuthNegative() throws DataAccessException{
        var authDao = new MySqlAuthDAO();
        deleteAuthTable();

        assertThrows(ResponseException.class, ()->
                authDao.getAuth("singer"));
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException, ResponseException {
        for (AuthDAO authDao : authDAOs) {
            authDao.createAuth(new AuthData("singer", "Damon"));
            authDao.createAuth(new AuthData("guitar", "Graham"));
            authDao.createAuth(new AuthData("drums", "Dave"));
            authDao.deleteAuth("guitar");
            assertTrue(authDao.getTableCount() == 2);
        }
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException{
        var authDao = new MySqlAuthDAO();
        deleteAuthTable();
        assertThrows(DataAccessException.class, ()->
                authDao.deleteAuth("drums"));
    }

    @Test
    public void getTableCountPositive() throws DataAccessException, ResponseException {
        for (AuthDAO authDao : authDAOs) {
            authDao.createAuth(new AuthData("1234", "Soph"));
            authDao.createAuth(new AuthData("5678", "Rish"));
            assertTrue(authDao.getTableCount() == 2);
        }
    }

    @Test
    public void getTableCountNegative() throws DataAccessException, ResponseException {
        var authDao = new MySqlAuthDAO();
        deleteAuthTable();
        assertThrows(ResponseException.class, ()->
                authDao.getTableCount());
    }

    private void deleteAuthTable() throws DataAccessException {
        var command = "DROP TABLE IF EXISTS auth_data";
        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(command);
    }
}
