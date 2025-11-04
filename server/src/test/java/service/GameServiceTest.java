package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameServiceTest {
    private static MemoryUserDAO userDAO = new MemoryUserDAO();
    private static MemoryGameDAO gameDAO = new MemoryGameDAO();
    private static MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final UserService USER_SERVICE = new UserService(authDAO, userDAO, gameDAO);

    static final GameService SERVICE = new GameService(authDAO, gameDAO);

    @BeforeEach
    void clearTests() throws DataAccessException {
        USER_SERVICE.clear();
    }

    @Test
    void createGamePositive() throws ResponseException, DataAccessException {
        authDAO.createAuth(new AuthData("starshine", "Geodude"));
        var name = new GameName( "UberSuperFunGame");
        var id = SERVICE.createGame("starshine", name);

        assertTrue(id.gameID() > 0);

        var name2 = new GameName(null);
        assertThrows(ResponseException.class, () ->
                SERVICE.createGame(null, name2));
    }

    @Test
    void listGames() throws ResponseException, DataAccessException {
        gameDAO.createGame("Peppermint Patty");
        gameDAO.createGame("Charlie Brown");
        gameDAO.createGame("Snoop Dogg");
        authDAO.createAuth(new AuthData("starshine", "Geodude"));
        var games = SERVICE.listGames("starshine");
        assertTrue(games != null);

        assertThrows(ResponseException.class, () ->
                SERVICE.listGames("superfast jellyfish"));
    }

    @Test
    void joinGame() throws ResponseException, DataAccessException {
        authDAO.createAuth(new AuthData("starshine", "Geodude"));
        gameDAO.createGame("Jeffrey");
        SERVICE.joinGame("starshine", new GameRequest(1, "WHITE"));

        assertTrue(gameDAO.getGame(1).whiteUsername() == "Geodude");
        assertThrows(ResponseException.class, () ->
                SERVICE.listGames("superfast jellyfish"));

    }
}
