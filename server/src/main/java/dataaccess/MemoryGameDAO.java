package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private int nextId = 1;
    final private HashMap<Integer, GameData> allGames = new HashMap<>();

    public void deleteInfo(){
        allGames.clear();
    }

    public GameID createGame(String gameName) {
        var gameId = nextId;
        var game = new GameData(gameId, null, null, gameName, new ChessGame());
        allGames.put(gameId, game);
        nextId ++;
        return new GameID(gameId);
    }
}
