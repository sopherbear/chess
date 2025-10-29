package dataaccess;

import exception.ResponseException;
import model.UserData;

public class MySQLUserDAO implements UserDAO{

    public UserData getUser(String username){
        return new UserData("temp", "temp", "temp");
    }

    public void createUser(UserData userData){

    }

    public void deleteInfo(){

    }

    public void verifyLogin(String username, String password) throws ResponseException{

    }

}
