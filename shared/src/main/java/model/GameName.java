package model;

import com.google.gson.Gson;

public record GameName(String gameName) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
