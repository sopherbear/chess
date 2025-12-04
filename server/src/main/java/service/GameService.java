package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.*;
import exception.ResponseException;
import dataaccess.DataAccessException;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameID createGame(String authToken, GameName gameName) throws ResponseException, DataAccessException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: Authorization not found\n");
        }

        var gameID = gameDAO.createGame(gameName.gameName());

        return gameID;
    }

    public void joinGame(String authToken, GameRequest gameRequest) throws ResponseException, DataAccessException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: Authorization not found\n");
        }

        var gamedata = gameDAO.getGame(gameRequest.gameID());

        gameDAO.addPlayer(gameRequest.gameID(), gameRequest.playerColor(), auth.username());
    }

    public GamesList listGames(String authToken) throws ResponseException, DataAccessException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: Authorization not found\n");
        }

        return new GamesList(gameDAO.listGames());
    }
}
