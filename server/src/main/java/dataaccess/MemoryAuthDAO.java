package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> allAuths = new HashMap<>();


    public AuthData getAuth(String authToken){
        if (allAuths.containsKey(authToken)){
            return allAuths.get(authToken);
        }
        else {
            return null;
        }
    }

    public void createAuth(AuthData authData) {
        allAuths.put(authData.authToken(), authData);
    }
}
