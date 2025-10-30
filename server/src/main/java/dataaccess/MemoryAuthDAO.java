package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
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

    public void deleteInfo() throws DataAccessException {
        allAuths.clear();
    }

    public void deleteAuth(String authToken) {
        allAuths.remove(authToken);
    }

    public int mapLen(){
        return allAuths.size();
    }
}
