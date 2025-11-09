package client;

import exception.ResponseException;
import model.LoginRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;
import facade.ServerFacade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = String.format("http://localhost:%d", port);
        facade = new ServerFacade(url);
    }

    @BeforeEach
    void clearAllTables() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testClearPositive() throws ResponseException{
        facade.register(new RegisterRequest("Noodle", "guitar", "kong.com"));
        facade.clear();
        var authData = facade.register(new RegisterRequest("Noodle", "guitar", "kong.com"));
        Assertions.assertTrue(authData.username().equals("Noodle"));
    }

    @Test
    public void testRegisterPositive() throws ResponseException{
        var authData = facade.register(new RegisterRequest("Noodle", "guitar", "kong.com"));
        assertTrue(authData.username().equals("Noodle"));
    }

    @Test
    public void testRegisterNegative() throws ResponseException{
        facade.register(new RegisterRequest("Chris", "singer", "musicofspheres@gmail.com"));
        assertThrows(ResponseException.class, ()->
                facade.register(new RegisterRequest("Chris", "somedude", "notreal@email.com")));
    }

    @Test
    public void testLoginPositive() throws ResponseException{
        var authData = facade.register(new RegisterRequest("Noodle", "guitar", "kong.com"));
        facade.logout(authData.authToken());
        var newAuth = facade.login(new LoginRequest("Noodle", "guitar"));
        assertTrue(newAuth.username().equals("Noodle"));
    }

}
