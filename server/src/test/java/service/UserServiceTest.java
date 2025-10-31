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
    static final UserService service = new UserService(authDAO, userDAO, gameDAO);

    @BeforeEach
    void clearTests() throws ResponseException, DataAccessException {
        service.clear();
    }


    @Test
    void clear() throws DataAccessException{
        userDAO.createUser(new UserData("Sophie", "1", "email"));
        gameDAO.createGame("coolGame");
        authDAO.createAuth(new AuthData("slfdklj", "jerry"));

        service.clear();
//        assertTrue(userDAO.mapLen()== 0);
//        assertTrue(authDAO.mapLen()== 0);
//        assertTrue(gameDAO.mapLen()== 0);
    }


    @Test
    void register() throws ResponseException, DataAccessException {
        var registerRequest = new RegisterRequest("Sophie", "badPassword", "nonemail@email.com");
        var auth = service.register(registerRequest);

        assertTrue(auth.username().equals("Sophie"));

        var registerRequest2 = new RegisterRequest("Sophie", "anotherPassword", "nonemail@email.com");
        assertThrows(ResponseException.class, () ->
                service.register(registerRequest2));
    }


    @Test
    void login() throws ResponseException, DataAccessException {
        var loginRequest = new LoginRequest("Sophie", "badPassword");

        assertThrows(ResponseException.class, () ->
                        service.login(loginRequest));

        userDAO.createUser(new UserData("Sophie", "badPassword", "email"));
        var auth = service.login(loginRequest);
        assertTrue(auth.username().equals("Sophie"));
    }


    @Test
    void logout() throws ResponseException, DataAccessException {
        authDAO.createAuth(new AuthData("jellybean", "Djo"));
        authDAO.createAuth(new AuthData("spinach", "popeye"));

        service.logout("jellybean");
        assertTrue(authDAO.mapLen() == 1);

        assertThrows(ResponseException.class, () ->
                service.logout("jellybean"));

    }


}
