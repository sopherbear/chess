package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO{


    public void deleteInfo(){
    }

    public GameID createGame(String gameName) {
        return new GameID(99);
    }

    public GameData getGame(int gameId) throws ResponseException {
        return new GameData(99, null, null, "jerry", new ChessGame());
    }

    public void addPlayer(int gameId, String playerColor, String username) throws ResponseException {
    }

    public Collection<GameData> listGames() {
        return new ArrayList<>();
    }

    public void clearCount() {

    }
}
