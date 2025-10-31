package dataaccess;

import exception.ResponseException;
import model.*;

public interface UserDAO {
    UserData getUser(String username) throws ResponseException;
    void createUser(UserData userData) throws DataAccessException;
    void deleteInfo() throws DataAccessException;
    void verifyLogin(String username, String password) throws ResponseException;
}
