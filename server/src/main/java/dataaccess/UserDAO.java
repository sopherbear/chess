package dataaccess;

import exception.ResponseException;
import model.*;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData userData);
}
