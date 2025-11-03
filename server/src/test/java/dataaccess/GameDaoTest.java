package dataaccess;

import Service.UserService;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
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
    private static  UserService userService = new UserService(authDAO, userDAO, gameSql);
    private static List<GameDAO> gameDAOs = List.of(gameSql, gameMemory);



    @BeforeEach
    void clearTests() throws ResponseException, DataAccessException {
        var command = """
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
            """;
        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(command);
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
    void createGameNegative() throws DataAccessException {
        deleteGameTable();
        assertThrows(DataAccessException.class, ()->
                gameSql.createGame("snaps"));
    }

    @Test
    void getGamePositive() throws ResponseException, DataAccessException{
        for (GameDAO dao : gameDAOs ) {
            dao.createGame("worstGame");
            dao.createGame("bestGame");
            var game = dao.getGame(1);
            assertTrue(game.gameName().equals("worstGame"));
        }
    }

    @Test
    void getGameNegative() throws DataAccessException{
        deleteGameTable();
        assertThrows(ResponseException.class, () ->
                gameSql.getGame(1));

    }

    @Test
    void addPlayerPositive() throws ResponseException, DataAccessException{
        for (GameDAO dao : gameDAOs) {
            dao.createGame("Plastic Beach");
            dao.addPlayer(1, "WHITE", "Murdoc");
            var game = dao.getGame(1);
            assertTrue(game.whiteUsername().equals("Murdoc"));
        }
    }

    @Test
    void addPlayerNegative() throws ResponseException, DataAccessException{
        for (GameDAO dao : gameDAOs) {
            dao.createGame("Plastic Beach");
            dao.addPlayer(1, "WHITE", "Murdoc");

            assertThrows(ResponseException.class, () ->
                    dao.addPlayer(1, "WHITE", "2D"));

        }
    }

    @Test
    void listGamesPositive() throws ResponseException, DataAccessException{
        for (GameDAO dao : gameDAOs) {
            dao.createGame("Wizard's Chess");
            dao.createGame("Lizard's Chess");
            dao.createGame("Your mom");
            assertTrue(dao.listGames().size() == 3);
        }
    }

    @Test
    void listGamesNegative() throws DataAccessException{
        deleteGameTable();
        assertThrows(ResponseException.class, () ->
                gameSql.listGames());


    }

    @Test
    void getTableCountPositive() throws ResponseException, DataAccessException {
        for (GameDAO dao: gameDAOs) {
            dao.createGame("checkers");
            dao.createGame("hammurabi");
            assertTrue(dao.getTableCount() == 2);
        }
    }

    @Test
    void getTableCountNegative() throws ResponseException, DataAccessException {
        deleteGameTable();

        assertThrows(ResponseException.class, () ->
                gameSql.getTableCount());

    }

    private void deleteGameTable() throws DataAccessException{
        var command = "DROP TABLE IF EXISTS game_data";
        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(command);
    }
}
