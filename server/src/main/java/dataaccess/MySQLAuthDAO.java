package dataaccess;

import model.*;

public class MySQLAuthDAO implements AuthDAO{

    public AuthData getAuth(String authToken) {
        return new AuthData("temp", "temp");
    }

    public void createAuth(AuthData authData) {

    }
    public void deleteInfo() {

    }
    public void deleteAuth(String authToken) {

    }
}
