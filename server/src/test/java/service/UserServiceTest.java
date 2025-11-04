package service;

import dataaccess.DataAccessException;
import model.*;
import Service.UserService;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private static MemoryUserDAO userDAO = new MemoryUserDAO();
    private static MemoryGameDAO gameDAO = new MemoryGameDAO();
    private static MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final UserService Service = new UserService(authDAO, userDAO, gameDAO);

    @BeforeEach
    void clearTests() throws DataAccessException {
        Service.clear();
    }


    @Test
    void clear() throws DataAccessException, ResponseException{
        userDAO.createUser(new UserData("Sophie", "1", "email"));
        gameDAO.createGame("coolGame");
        authDAO.createAuth(new AuthData("slfdklj", "jerry"));

        Service.clear();
        assertTrue(userDAO.getTableCount()== 0);
        assertTrue(authDAO.getTableCount()== 0);
        assertTrue(gameDAO.getTableCount()== 0);
    }


    @Test
    void register() throws ResponseException, DataAccessException {
        var registerRequest = new RegisterRequest("Sophie", "badPassword", "nonemail@email.com");
        var auth = Service.register(registerRequest);

        assertTrue(auth.username().equals("Sophie"));

        var registerRequest2 = new RegisterRequest("Sophie", "anotherPassword", "nonemail@email.com");
        assertThrows(ResponseException.class, () ->
                Service.register(registerRequest2));
    }


    @Test
    void login() throws ResponseException, DataAccessException {
        var loginRequest = new LoginRequest("Sophie", "badPassword");

        assertThrows(ResponseException.class, () ->
                        Service.login(loginRequest));

        userDAO.createUser(new UserData("Sophie", "badPassword", "email"));
        var auth = Service.login(loginRequest);
        assertTrue(auth.username().equals("Sophie"));
    }


    @Test
    void logout() throws ResponseException, DataAccessException {
        authDAO.createAuth(new AuthData("jellybean", "Djo"));
        authDAO.createAuth(new AuthData("spinach", "popeye"));

        Service.logout("jellybean");
        assertTrue(authDAO.getTableCount() == 1);

        assertThrows(ResponseException.class, () ->
                Service.logout("jellybean"));

    }


}
