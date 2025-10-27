package Service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.*;
import exception.ResponseException;
import dataaccess.DataAccessException;

import java.util.Collection;

public class GameService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public GameID createGame(String authToken, GameRequest gameRequest) throws ResponseException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: Authorization not found");
        }

        var gameID = gameDAO.createGame(gameRequest.gameName());

        return gameID;
    }

    public void joinGame(String authToken, GameRequest gameRequest) throws ResponseException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: Authorization not found");
        }

        var game = gameDAO.getGame(gameRequest.gameID());

        gameDAO.addPlayer(gameRequest.gameID(), gameRequest.playerColor(), auth.username());
    }

    public GamesList listGames(String authToken) throws ResponseException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: Authorization not found");
        }

        return new GamesList(gameDAO.listGames());
    }
}
