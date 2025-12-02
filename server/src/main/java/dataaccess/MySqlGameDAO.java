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
        var statement = "INSERT INTO game_data (gameName, game) VALUES (?, ?)";

        var updateExecutor = new ExecuteDatabaseUpdates();
        String jsonChess = new Gson().toJson(new ChessGame());
        var gameID = updateExecutor.executeUpdate(statement, gameName, jsonChess);
        return new GameID(gameID);
    }

    public GameData getGame(int gameId) throws ResponseException, DataAccessException {
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
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Error: Unable to read data: %s\n", e.getMessage()));
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid gameID");
    }

    public void addPlayer(int gameId, String playerColor, String username) throws ResponseException, DataAccessException {
        var gameData = getGame(gameId);
        if (gameData == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid gameId\n");
        }
        var canAddPlayer = colorAvailable(gameId, playerColor);
        if (!canAddPlayer) {
            throw new ResponseException(ResponseException.Code.AlreadyTakenError, "Error: Color already taken\n");
        }

        String command;
        if (playerColor.equals("WHITE")) {
            command = "UPDATE game_data SET whiteUsername=? WHERE gameID=?";
        } else {
            command = "UPDATE game_data SET blackUsername=? WHERE gameID=?";
        }

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(command, username, gameId);

    }

    public Collection<GameData> listGames() throws ResponseException, DataAccessException{
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
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s\n", e.getMessage()));
        }
        return result;
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var gameId = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var game = rs.getString("game");
        ChessGame chessGame = new Gson().fromJson(game, ChessGame.class);
        return new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame);
    }

    private Boolean readPlayerColor(ResultSet rs, String column) throws SQLException {
        var playerColor = rs.getString(column);
        return playerColor == null;
    }

    private int getCount(ResultSet rs) throws SQLException{
        return rs.getInt(1);
    }

    private Boolean colorAvailable(int gameId, String playerColor) throws ResponseException, DataAccessException{
        String wantedColumn;
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement;
            if (playerColor.equals("WHITE")) {
                statement = "SELECT whiteUsername FROM game_data WHERE gameID=?";
                wantedColumn= "whiteUsername";
            } else {
                statement = "SELECT blackUsername FROM game_data WHERE gameID = ?";
                wantedColumn = "blackUsername";
            }
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readPlayerColor(rs, wantedColumn);
                    }
                }
            }
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s\n", e.getMessage()));
        }
        return null;
    }

    public int getTableCount() throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM game_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return getCount(rs);
                    }
                }
            }
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read game data: %s\n", e.getMessage()));
        }
        throw new ResponseException(ResponseException.Code.ServerError, "Error: Unable to count game data: %s\n");
    }

}
