package Service;

import dataaccess.*;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;
import model.AuthData;
import exception.ResponseException;
import dataaccess.DataAccessException;

import javax.xml.crypto.Data;
import java.util.UUID;


public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }


    public AuthData register(RegisterRequest registerRequest) throws ResponseException, DataAccessException{
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

    public void clear() throws DataAccessException{
        gameDAO.deleteInfo();
        userDAO.deleteInfo();
        authDAO.deleteInfo();
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException, DataAccessException {
        var username = loginRequest.username();
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

    public void logout(String authToken) throws ResponseException, DataAccessException{
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.UnauthorizedError, "Error: Authorization not found");
        }
        authDAO.deleteAuth(authToken);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

