package model;

import com.google.gson.Gson;

public record GameRequest(int gameID, String playerColor, String gameName) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
