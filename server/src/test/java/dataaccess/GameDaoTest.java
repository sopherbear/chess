package dataaccess;

import Service.UserService;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.util.List;

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

        gameMemory.createGame("newGame");
        gameMemory.deleteInfo();
        assertTrue(gameSql.getTableCount() == 0);
    }


    @Test
    void createGamePositive() throws ResponseException, DataAccessException {
        gameSql.createGame("bestGame");
        assertTrue(gameSql.getTableCount() == 1);
    }

    @Test
    void createGameNegative() throws ResponseException, DataAccessException {
        List<GameDAO> gameDAOs = List.of(gameSql, gameMemory);

        for (GameDAO dao : gameDAOs ) {
            dao.createGame("worstGame");
            dao.createGame("bestGame");
            assertThrows(ResponseException.class, () ->
                    dao.getGame(18));

        }
    }

//    @Test
//    void


}
