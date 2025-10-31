package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO{


    public void deleteInfo() throws DataAccessException{
        var statement = "TRUNCATE game_data";

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(statement);
    }

    public GameID createGame(String gameName) throws DataAccessException{
        var statement = "INSERT INTO user_data (gameName, game) VALUES (?, ?)";

        var updateExecutor = new ExecuteDatabaseUpdates();
        String jsonChess = new Gson().toJson(new ChessGame());
        var gameID = updateExecutor.executeUpdate(statement, gameName, jsonChess);
        return new GameID(gameID);
    }

    public GameData getGame(int gameId) throws ResponseException {
        return new GameData(99, null, null, "jerry", new ChessGame());
    }

    public void addPlayer(int gameId, String playerColor, String username) throws ResponseException {
    }

    public Collection<GameData> listGames() {
        return new ArrayList<>();
    }
}
