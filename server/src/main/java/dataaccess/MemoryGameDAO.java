package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<String, GameData> allGames = new HashMap<>();

    public void deleteInfo(){
        allGames.clear();
    }
}
