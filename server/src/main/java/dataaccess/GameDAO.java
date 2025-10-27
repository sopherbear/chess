package dataaccess;

import exception.ResponseException;
import model.*;

public interface GameDAO {
    void deleteInfo();
    GameID createGame(String gameName);
}
