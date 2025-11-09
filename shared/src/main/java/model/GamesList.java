package model;

import com.google.gson.Gson;

import java.util.Collection;

public record GamesList(Collection<GameData> games) {

    public String toString() {
        return new Gson().toJson(this);
    }

    public int getSize() {
        return games.size();
    }
}
