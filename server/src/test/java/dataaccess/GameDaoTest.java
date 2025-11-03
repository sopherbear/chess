package dataaccess;

import Service.UserService;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameDaoTest {
    private static GameDAO gameSql = new MySqlGameDAO();
    private static GameDAO gameMemory = new MemoryGameDAO();
    private static MemoryUserDAO userDAO = new MemoryUserDAO();
    private static MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final UserService userService = new UserService(authDAO, userDAO, gameSql);



    @BeforeEach
    void clearTests() throws ResponseException, DataAccessException {
        userService.clear();
        gameMemory.deleteInfo();
    }

    @Test
    void deleteInfoPostive() throws ResponseException, DataAccessException {
        gameSql.createGame("newGame");
        gameSql.deleteInfo();
        assertTrue(gameSql.getTableCount() == 0);
    }




}
