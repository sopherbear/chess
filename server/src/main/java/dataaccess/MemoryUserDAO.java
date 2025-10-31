package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.*;



public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username){
        if (users.containsKey(username)){
            return users.get(username);
        }
        else {
            return null;
        }
    }

    public void createUser(UserData userData) throws DataAccessException{
        users.put(userData.username(), userData);
    }

    public void deleteInfo() throws DataAccessException{
        users.clear();
    }

    public void verifyLogin(String username, String password) throws ResponseException{
        var user = users.get(username);
        if (!user.password().equals(password)) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: unauthorized");
        }
    }

    public int mapLen(){
        return users.size();
    }
}
