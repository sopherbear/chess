package Service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.UserData;
import model.AuthData;
import exception.ResponseException;
import dataaccess.DataAccessException;


public class UserService {
    // Will likely need to move this later
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public UserData getUser(String userName){
        return userDAO.getUser(userName);
    }

    public void createUser(UserData userData) {
        userDAO.createUser(userData);
    }
}
