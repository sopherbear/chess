package dataaccess;

import java.util.HashMap;
import model.*;

import javax.xml.crypto.Data;


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

    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }


}
