package dataaccess;

import exception.ResponseException;
import model.*;

import java.util.Collection;

public interface GameDAO {
    void deleteInfo() throws DataAccessException;
    GameID createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameId) throws ResponseException, DataAccessException;
    void addPlayer(int gameId, String playerColor, String username) throws ResponseException, DataAccessException;
    Collection<GameData> listGames() throws ResponseException, DataAccessException;
}
