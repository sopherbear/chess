package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private int nextId = 1;
    final private HashMap<Integer, GameData> allGames = new HashMap<>();

    public void deleteInfo() throws DataAccessException{
        allGames.clear();
        nextId = 1;
    }

    public GameID createGame(String gameName) throws DataAccessException{
        var gameId = nextId;
        var game = new GameData(gameId, null, null, gameName, new ChessGame());
        allGames.put(gameId, game);
        nextId ++;
        return new GameID(gameId);
    }

    public GameData getGame(int gameId) throws ResponseException{
        if (allGames.containsKey(gameId)){
            return allGames.get(gameId);
        }
        else {
           throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid gameID");
        }
    }

    public void addPlayer(int gameId, String playerColor, String username) throws ResponseException{
        var game = allGames.get(gameId);
        GameData updatedGame;
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new ResponseException(ResponseException.Code.AlreadyTakenError, "Error: White player already taken");
            }
            updatedGame = new GameData(gameId, username, game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new ResponseException(ResponseException.Code.AlreadyTakenError, "Error: White player already taken");
            }
            updatedGame = new GameData(gameId, game.whiteUsername(), username, game.gameName(), game.game());
        }

        allGames.put(gameId, updatedGame);
    }

    public Collection<GameData> listGames() {
        Collection<GameData> games = new ArrayList<>();
        for (GameData game: allGames.values()) {
            games.add(game);
        }
        return games;
    }

    public int mapLen(){
        return allGames.size();
    }

}
