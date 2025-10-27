package Service;

import dataaccess.*;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;
import model.AuthData;
import exception.ResponseException;
import dataaccess.DataAccessException;

import java.util.UUID;


public class UserService {
    // Will likely need to move this later
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }


    public AuthData register(RegisterRequest registerRequest) throws ResponseException{
        var userName = registerRequest.username();
        var user = userDAO.getUser(userName);
        if (user != null) {
            throw new ResponseException(ResponseException.Code.AlreadyTakenError, "Error: username already taken");
        }
        UserData user2add = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(user2add);

        var newToken = generateToken();
        AuthData authData = new AuthData(newToken, userName);
        authDAO.createAuth(authData);
        return authData;
    }

    public void clear(){
        gameDAO.deleteInfo();
        userDAO.deleteInfo();
        authDAO.deleteInfo();
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException{
        var username = loginRequest.username();
        if (username == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: username cannot be null");
        }
        var userData = userDAO.getUser(username);
        if (userData == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: username does not exist");
        }
        userDAO.verifyLogin(username, loginRequest.password());
        var newToken = generateToken();
        var newAuthData = new AuthData(newToken, username);
        authDAO.createAuth(newAuthData);
        return newAuthData;
    }

//    public void logout(String AuthToken) throws ResponseException{
//        AuthDAO.
//    }


    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

