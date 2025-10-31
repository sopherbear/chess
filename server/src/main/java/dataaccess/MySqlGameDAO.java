package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game_data WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void addPlayer(int gameId, String playerColor, String username) throws ResponseException {
    }

    public Collection<GameData> listGames() throws ResponseException{
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGameData(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public GameData readGameData(ResultSet rs) throws SQLException {
        var gameId = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var game = rs.getString("game");
        ChessGame chessGame = new Gson().fromJson(game, ChessGame.class);
        return new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame);
    }
}
