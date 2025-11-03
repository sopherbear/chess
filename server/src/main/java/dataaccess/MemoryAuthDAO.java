package dataaccess;

import exception.ResponseException;
import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> allAuths = new HashMap<>();


    public AuthData getAuth(String authToken) throws ResponseException, DataAccessException {
        if (allAuths.containsKey(authToken)){
            return allAuths.get(authToken);
        }
        else {
            return null;
        }
    }

    public void createAuth(AuthData authData) throws DataAccessException{
        allAuths.put(authData.authToken(), authData);
    }

    public void deleteInfo() throws DataAccessException {
        allAuths.clear();
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        allAuths.remove(authToken);
    }

    public int getTableCount() throws ResponseException{
        return allAuths.size();
    }
}
