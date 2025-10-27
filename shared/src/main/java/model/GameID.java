package model;

import com.google.gson.Gson;

public record GameID(int gameID) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
